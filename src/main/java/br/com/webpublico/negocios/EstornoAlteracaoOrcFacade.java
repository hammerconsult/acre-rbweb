/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.AlteracaoOrcEstornoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Claudio
 */
@Stateless
public class EstornoAlteracaoOrcFacade extends SuperFacadeContabil<EstornoAlteracaoOrc> {

    @EJB
    private AlteracaoORCFacade alteracaoORCFacade;
    @EJB
    private SuplementacaoOrcFacade suplementacaoOrcFacade;
    @EJB
    private AnulacaoOrcFacade anulacaoOrcFacade;
    @EJB
    private ReceitaAlteracaoOrcFacade receitaAlteracaoOrcFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private CotaOrcamentariaFacade cotaOrcamentariaFacade;
    @EJB
    private SaldoReceitaORCFacade saldoReceitaORCFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfigAlteracaoOrcFacade configAlteracaoOrcFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EstornoAlteracaoOrcFacade() {
        super(EstornoAlteracaoOrc.class);
    }

    @Override
    public EstornoAlteracaoOrc recuperar(Object id) {
        EstornoAlteracaoOrc estornoAlteracaoOrc = em.find(EstornoAlteracaoOrc.class, id);
        estornoAlteracaoOrc.getListaEstornoAnulacaoOrc().size();
        estornoAlteracaoOrc.getListaEstornoReceitaAlteracaoOrc().size();
        estornoAlteracaoOrc.getListaEstornoSuplementacaoOrc().size();
        return estornoAlteracaoOrc;
    }


    public String retornaUltimoCodigoEstornoAlteracaoOrc() {
        try {
            String hql = "select coalesce(max(to_number(est.codigo)),0)+1 from EstornoAlteracaoOrc est";
            Query q = em.createNativeQuery(hql);
            q.setMaxResults(1);
            BigDecimal valor = (BigDecimal) q.getSingleResult();
            return valor.toString();
        } catch (Exception e) {

            return "1";
        }
    }

    @Override
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void salvarNovo(EstornoAlteracaoOrc entity) {
        try {
            entity.setCodigo(retornaUltimoCodigoEstornoAlteracaoOrc());
            movimentarAlteracaoOrc(entity);
            entity = em.merge(entity);
            gerarSaldoOrcamentario(entity);
            gerarContabilizacao(entity);
            salvarNotificaoEstornoAlteracaoORC(entity);
            portalTransparenciaNovoFacade.salvarPortal(new AlteracaoOrcEstornoPortal(entity));
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void gerarSaldoOrcamentario(EstornoAlteracaoOrc entity) {
        estornarSuplementacao(entity);
        estornaAnulacaoOrc(entity);
        estornaReceitaAlteracaoOrc(entity);
    }

    private void movimentarAlteracaoOrc(EstornoAlteracaoOrc entity) {
        AlteracaoORC alteracaoORC = alteracaoORCFacade.recuperar(entity.getAlteracaoORC().getId());
        alteracaoORC.setStatus(StatusAlteracaoORC.ESTORNADA);
        if (!alteracaoORC.getSuplementacoesORCs().isEmpty()) {
            movimentarSuplementacao(alteracaoORC);
        }
        if (!alteracaoORC.getAnulacoesORCs().isEmpty()) {
            movimentarAnulacao(alteracaoORC);
        }
        if (!alteracaoORC.getReceitasAlteracoesORCs().isEmpty()) {
            movimentarReceitaAlteracaoORC(alteracaoORC);
        }
        em.merge(alteracaoORC);
    }

    private void movimentarAnulacao(AlteracaoORC alteracaoORC) {
        for (AnulacaoORC anulacaoORC : alteracaoORC.getAnulacoesORCs()) {
            anulacaoORC.setSaldo(BigDecimal.ZERO);
        }
    }

    private void movimentarSuplementacao(AlteracaoORC alteracaoORC) {
        for (SuplementacaoORC suplementacaoORC : alteracaoORC.getSuplementacoesORCs()) {
            suplementacaoORC.setSaldo(BigDecimal.ZERO);
        }
    }


    private void movimentarReceitaAlteracaoORC(AlteracaoORC alteracaoORC) {
        for (ReceitaAlteracaoORC receitaAlteracaoORC : alteracaoORC.getReceitasAlteracoesORCs()) {
            receitaAlteracaoORC.setSaldo(BigDecimal.ZERO);
        }
    }

    private void gerarContabilizacao(EstornoAlteracaoOrc entity) {

        for (EstornoSuplementacaoOrc suplementacao : entity.getListaEstornoSuplementacaoOrc()) {
            contabilizarEstornoSuplementacao(suplementacao);
        }
        for (EstornoAnulacaoOrc anulacao : entity.getListaEstornoAnulacaoOrc()) {
            contabilizarEstornoAnulacao(anulacao);
        }
        for (EstornoReceitaAlteracaoOrc receita : entity.getListaEstornoReceitaAlteracaoOrc()) {
            contabilizarEstornoReceita(receita);
        }
    }

    private void contabilizarEstornoSuplementacao(EstornoSuplementacaoOrc entity) {

        try {
            TipoDespesaORC tipoDespesaORC = entity.getSuplementacaoORC().getTipoDespesaORC();
            OrigemSuplementacaoORC origemSuplementacaoORC = entity.getSuplementacaoORC().getOrigemSuplemtacao();
            ConfigAlteracaoOrc configuracao = configAlteracaoOrcFacade.recuperarConfigEventoCreditoSuplementar(tipoDespesaORC, origemSuplementacaoORC, TipoLancamento.ESTORNO, entity.getEstornoAlteracaoOrc().getDataEstorno());
            if (configuracao != null) {
                entity.setEventoContabil(configuracao.getEventoContabil());
            } else {
                throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Crédito Adicional.");
            }

            gerarHistoricoSuplementacao(entity);

            ParametroEvento parametroEvento = new ParametroEvento();

            parametroEvento.setDataEvento(entity.getEstornoAlteracaoOrc().getDataEstorno());
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setUnidadeOrganizacional(entity.getSuplementacaoORC().getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            parametroEvento.setExercicio(entity.getEstornoAlteracaoOrc().getExercicio());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> listaObj = new ArrayList<ObjetoParametro>();
            listaObj.add(new ObjetoParametro(entity.getId().toString(), EstornoSuplementacaoOrc.class.getSimpleName(), item));
            listaObj.add(new ObjetoParametro(entity.getSuplementacaoORC().getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa().getId().toString(), ContaDespesa.class.getSimpleName(), item));
            listaObj.add(new ObjetoParametro(entity.getSuplementacaoORC().getOrigemSuplemtacao().name(), OrigemSuplementacaoORC.class.getSimpleName(), item));
            listaObj.add(new ObjetoParametro(entity.getSuplementacaoORC().getFonteDespesaORC().getId().toString(), FonteDespesaORC.class.getSimpleName(), item));
            listaObj.add(new ObjetoParametro(entity.getSuplementacaoORC().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getId().toString(), FonteDeRecursos.class.getSimpleName(), item));
            item.setObjetoParametros(listaObj);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void contabilizarEstornoAnulacao(EstornoAnulacaoOrc entity) {
        try {
            TipoDespesaORC tipoDespesaORC = entity.getAnulacaoORC().getTipoDespesaORC();
            ConfigAlteracaoOrc configuracao = configAlteracaoOrcFacade.recuperarConfigEventoAnulacaoCredito(tipoDespesaORC, TipoLancamento.ESTORNO, entity.getEstornoAlteracaoOrc().getDataEstorno());
            if (configuracao != null) {
                entity.setEventoContabil(configuracao.getEventoContabil());
            } else {
                throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Anulação de Crédito.");
            }
            gerarHistoricoAnulacao(entity);

            ParametroEvento parametroEvento = new ParametroEvento();

            parametroEvento.setDataEvento(entity.getEstornoAlteracaoOrc().getDataEstorno());
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setUnidadeOrganizacional(entity.getAnulacaoORC().getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            parametroEvento.setExercicio(entity.getEstornoAlteracaoOrc().getExercicio());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());


            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> listaObj = new ArrayList<ObjetoParametro>();
            listaObj.add(new ObjetoParametro(entity.getId().toString(), EstornoAnulacaoOrc.class.getSimpleName(), item));
            listaObj.add(new ObjetoParametro(entity.getAnulacaoORC().getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa().getId().toString(), ContaDespesa.class.getSimpleName(), item));
            listaObj.add(new ObjetoParametro(entity.getAnulacaoORC().getFonteDespesaORC().getId().toString(), FonteDespesaORC.class.getSimpleName(), item));
            listaObj.add(new ObjetoParametro(entity.getAnulacaoORC().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getId().toString(), FonteDeRecursos.class.getSimpleName(), item));
            item.setObjetoParametros(listaObj);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void contabilizarEstornoReceita(EstornoReceitaAlteracaoOrc entity) {
        try {
            ConfigAlteracaoOrc configuracao = configAlteracaoOrcFacade.buscarConfigEventoReceita(TipoLancamento.ESTORNO, entity.getReceitaAlteracaoORC());
            if (configuracao != null) {
                entity.setEventoContabil(configuracao.getEventoContabil());
            } else {
                throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Receita.");
            }
            gerarHistoricoReceitaAlteracaoOrc(entity);

            ParametroEvento parametroEvento = new ParametroEvento();

            parametroEvento.setDataEvento(entity.getEstornoAlteracaoOrc().getDataEstorno());
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setUnidadeOrganizacional(entity.getReceitaAlteracaoORC().getReceitaLOA().getEntidade());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            parametroEvento.setExercicio(entity.getReceitaAlteracaoORC().getReceitaLOA().getLoa().getLdo().getExercicio());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> listaObj = new ArrayList<ObjetoParametro>();
            listaObj.add(new ObjetoParametro(entity.getId().toString(), EstornoReceitaAlteracaoOrc.class.getSimpleName(), item));
            listaObj.add(new ObjetoParametro(entity.getReceitaAlteracaoORC().getReceitaLOA().getContaDeReceita().getId().toString(), ContaReceita.class.getSimpleName(), item));
            item.setObjetoParametros(listaObj);

            parametroEvento.getItensParametrosEvento().add(item);
            contabilizacaoFacade.contabilizar(parametroEvento);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }


    private void estornarSuplementacao(EstornoAlteracaoOrc entity) {
        for (EstornoSuplementacaoOrc suplem : entity.getListaEstornoSuplementacaoOrc()) {
            //Comentado por solicitação do gestor do orçamento, mas pode haver solicitação de retorno.
//            FonteDespesaORC fonteDespesaORC = fonteDespesaORCFacade.salvarRetornandoComNovaTransacao(suplem.getSuplementacaoORC().getFonteDespesaORC());
//            GrupoOrcamentario grupoOrcamentario = suplem.getSuplementacaoORC().getFonteDespesaORC().getGrupoOrcamentario();
//            cotaOrcamentariaFacade.debitaValorNaCotaOrcamentaria(fonteDespesaORC, suplem.getSuplementacaoORC().getMes(), suplem.getValor(), grupoOrcamentario);

            UnidadeOrganizacional unidadeDespesa = suplem.getSuplementacaoORC().getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional();
            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                suplem.getSuplementacaoORC().getFonteDespesaORC(),
                OperacaoORC.SUPLEMENTACAO,
                TipoOperacaoORC.ESTORNO,
                suplem.getValor(),
                entity.getDataEstorno(),
                unidadeDespesa,
                suplem.getId().toString(),
                suplem.getClass().getSimpleName(),
                entity.getCodigo(),
                suplem.getHistoricoRazao());
            saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
        }
    }


    private void estornaAnulacaoOrc(EstornoAlteracaoOrc selecionado) {
        for (EstornoAnulacaoOrc anulacao : selecionado.getListaEstornoAnulacaoOrc()) {
            anulacao.getAnulacaoORC().setSaldo(BigDecimal.ZERO);
            //Comentado por solicitação do gestor do orçamento, mas pode haver solicitação de retorno.
//            FonteDespesaORC fonteDespesaORC = fonteDespesaORCFacade.salvarRetornandoComNovaTransacao(anulacao.getAnulacaoORC().getFonteDespesaORC());
//            GrupoOrcamentario grupoOrcamentario = anulacao.getAnulacaoORC().getFonteDespesaORC().getGrupoOrcamentario();
//            cotaOrcamentariaFacade.creditaValorNaCotaOrcamentaria(fonteDespesaORC, anulacao.getAnulacaoORC().getMes(), anulacao.getValor(), grupoOrcamentario);

            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                anulacao.getAnulacaoORC().getFonteDespesaORC(),
                OperacaoORC.ANULACAO,
                TipoOperacaoORC.ESTORNO,
                anulacao.getValor(),
                selecionado.getDataEstorno(),
                anulacao.getAnulacaoORC().getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
                anulacao.getId().toString(),
                anulacao.getClass().getSimpleName(),
                selecionado.getCodigo(),
                anulacao.getHistoricoRazao());
            saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
        }
    }

    private void estornaReceitaAlteracaoOrc(EstornoAlteracaoOrc selecionado) {
        for (EstornoReceitaAlteracaoOrc e : selecionado.getListaEstornoReceitaAlteracaoOrc()) {
            BigDecimal novoSaldo = e.getReceitaAlteracaoORC().getSaldo().subtract(e.getValor());
            e.getReceitaAlteracaoORC().setSaldo(novoSaldo);
            TipoSaldoReceitaORC tipoSaldoReceitaORC;
            if (e.getReceitaAlteracaoORC().getTipoAlteracaoORC().equals(TipoAlteracaoORC.PREVISAO)) {
                tipoSaldoReceitaORC = TipoSaldoReceitaORC.ALTERACAOORC_ANULACAO;
            } else {
                tipoSaldoReceitaORC = TipoSaldoReceitaORC.ALTERACAOORC_ADICIONAL;
            }
            saldoReceitaORCFacade.geraSaldo(tipoSaldoReceitaORC, selecionado.getDataEstorno(), e.getReceitaAlteracaoORC().getReceitaLOA().getEntidade(), e.getReceitaAlteracaoORC().getReceitaLOA().getContaDeReceita(), e.getReceitaAlteracaoORC().getFonteDeRecurso(), e.getValor());
        }
    }

    private void salvarNotificaoEstornoAlteracaoORC(EstornoAlteracaoOrc entity) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("A Alteração Orçamentária Nº " + entity.getAlteracaoORC().getCodigo() + " foi estornada na data " + DataUtil.getDataFormatada(entity.getDataEstorno()));
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTitulo("Alteração Orçamentária Estornada");
        notificacao.setTipoNotificacao(TipoNotificacao.RETORNO_ALTERACAO_ORCAMENTARIA);
        notificacao.setUnidadeOrganizacional(entity.getAlteracaoORC().getUnidadeOrganizacionalOrc());
        notificacao.setLink("/alteracao-orcamentaria/ver/" + entity.getAlteracaoORC().getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public List<EstornoSuplementacaoOrc> recuperarEstornoSuplementacao(AlteracaoORC alteracaoORC) {
        String sql = "";
        Query q = em.createNativeQuery(sql, EstornoSuplementacaoOrc.class);
        q.setParameter("idAlteracao", alteracaoORC.getId());
        if (q.getResultList().isEmpty() || q.getResultList() == null) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public void gerarHistoricoSuplementacao(EstornoSuplementacaoOrc suple) {
        suple.gerarHistoricos();
    }

    public void gerarHistoricoAnulacao(EstornoAnulacaoOrc anul) {
        anul.gerarHistoricos();
    }

    public void gerarHistoricoReceitaAlteracaoOrc(EstornoReceitaAlteracaoOrc rec) {
        rec.gerarHistoricos();
    }


    public AlteracaoORCFacade getAlteracaoORCFacade() {
        return alteracaoORCFacade;
    }

    public SuplementacaoOrcFacade getSuplementacaoOrcFacade() {
        return suplementacaoOrcFacade;
    }

    public CotaOrcamentariaFacade getCotaOrcamentariaFacade() {
        return cotaOrcamentariaFacade;
    }

    public SaldoFonteDespesaORCFacade getSaldoFonteDespesaORCFacade() {
        return saldoFonteDespesaORCFacade;
    }

    public SaldoReceitaORCFacade getSaldoReceitaORCFacade() {
        return saldoReceitaORCFacade;
    }

    public AnulacaoOrcFacade getAnulacaoOrcFacade() {
        return anulacaoOrcFacade;
    }

    public ReceitaAlteracaoOrcFacade getReceitaAlteracaoOrcFacade() {
        return receitaAlteracaoOrcFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        if (entidadeContabil instanceof EstornoSuplementacaoOrc) {
            contabilizarEstornoSuplementacao((EstornoSuplementacaoOrc) entidadeContabil);
        }
        if (entidadeContabil instanceof EstornoAnulacaoOrc) {
            contabilizarEstornoAnulacao((EstornoAnulacaoOrc) entidadeContabil);
        }
        if (entidadeContabil instanceof EstornoReceitaAlteracaoOrc) {
            contabilizarEstornoReceita((EstornoReceitaAlteracaoOrc) entidadeContabil);
        }
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<EntidadeContabil> buscarEstornoAlteracaoORCDespesa(Exercicio exercicio, EventosReprocessar eventosReprocessar) {
        List<EntidadeContabil> retorno = Lists.newArrayList();
        retorno.addAll(buscarEstornoSuplementacoes(exercicio, eventosReprocessar));
        retorno.addAll(buscarEstornoAnulacoes(exercicio, eventosReprocessar));
        return retorno;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<EntidadeContabil> buscarAlteracaoORCReceita(Exercicio exercicio, EventosReprocessar eventosReprocessar) {
        List<EntidadeContabil> retorno = Lists.newArrayList();
        retorno.addAll(buscarEstornoReceitas(exercicio, eventosReprocessar));
        return retorno;
    }

    private List<EntidadeContabil> buscarEstornoSuplementacoes(Exercicio exercicio, EventosReprocessar eventosReprocessar) {
        try {
            String sql = "select supl.* from estornoalteracaoorc alt "
                + "   inner join exercicio ex on alt.exercicio_id = ex.id "
                + "   inner join estornosuplementacaoorc supl on alt.id = supl.estornoAlteracaoOrc_id"
                + "   inner join suplementacaoorc suplementacao on supl.suplementacaoorc_id = suplementacao.id "
                + "   inner join fontedespesaorc font on suplementacao.fontedespesaorc_id = font.id"
                + "   inner join despesaorc desp on font.despesaorc_id = desp.id"
                + "   inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id"
                + "   where trunc(alt.dataEstorno) between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')";
            if (eventosReprocessar.hasUnidades()) {
                sql += " and prov.unidadeOrganizacional_id in (:unidades)";
            }
            sql += " order by trunc(alt.dataEstorno) asc ";
            Query consulta = em.createNativeQuery(sql, EstornoSuplementacaoOrc.class);
            consulta.setParameter("di", DataUtil.getDataFormatada(eventosReprocessar.getDataInicial()));
            consulta.setParameter("df", DataUtil.getDataFormatada(eventosReprocessar.getDataFinal()));
            if (eventosReprocessar.hasUnidades()) {
                consulta.setParameter("unidades", eventosReprocessar.getIdUnidades());
            }
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<EntidadeContabil> buscarEstornoAnulacoes(Exercicio exercicio, EventosReprocessar eventosReprocessar) {
        try {
            String sql = "select supl.* from estornoalteracaoorc alt "
                + "   inner join exercicio ex on alt.exercicio_id = ex.id "
                + "   inner join estornoanulacaoorc supl on alt.id = supl.estornoAlteracaoOrc_id"
                + "   inner join anulacaoorc anulacao on supl.anulacaoorc_id = anulacao.id "
                + "   inner join fontedespesaorc font on anulacao.fontedespesaorc_id = font.id"
                + "   inner join despesaorc desp on font.despesaorc_id = desp.id"
                + "   inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id"
                + "   where trunc(alt.dataEstorno) between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')";
            if (eventosReprocessar.hasUnidades()) {
                sql += " and prov.unidadeOrganizacional_id in (:unidades)";
            }
            sql += " order by trunc(alt.dataEstorno) asc ";
            Query consulta = em.createNativeQuery(sql, EstornoAnulacaoOrc.class);
            consulta.setParameter("di", DataUtil.getDataFormatada(eventosReprocessar.getDataInicial()));
            consulta.setParameter("df", DataUtil.getDataFormatada(eventosReprocessar.getDataFinal()));
            if (eventosReprocessar.hasUnidades()) {
                consulta.setParameter("unidades", eventosReprocessar.getIdUnidades());
            }
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<EntidadeContabil> buscarEstornoReceitas(Exercicio exercicio, EventosReprocessar eventosReprocessar) {
        try {
            String sql = "select supl.* from estornoalteracaoorc alt "
                + "   inner join exercicio ex on alt.exercicio_id = ex.id "
                + "   inner join estornoreceitaalteracaoorc supl on alt.id = supl.estornoAlteracaoOrc_id"
                + "   inner join receitaalteracaoorc receita on supl.receitaalteracaoorc_id = receita.id"
                + "   inner join receitaloa rec on receita.receitaloa_id = rec.id"
                + "   where ex.id = :ex" +
                "       and trunc(alt.dataEstorno) between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')";
            if (eventosReprocessar.hasUnidades()) {
                sql += " and rec.entidade_id in (:unidades)";
            }
            sql += " order by trunc(alt.dataEstorno) asc ";
            Query consulta = em.createNativeQuery(sql, EstornoReceitaAlteracaoOrc.class);
            consulta.setParameter("ex", exercicio);
            consulta.setParameter("di", DataUtil.getDataFormatada(eventosReprocessar.getDataInicial()));
            consulta.setParameter("df", DataUtil.getDataFormatada(eventosReprocessar.getDataFinal()));
            if (eventosReprocessar.hasUnidades()) {
                consulta.setParameter("unidades", eventosReprocessar.getIdUnidades());
            }
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<EstornoAlteracaoOrc> buscarEstornoAlteracaoOrcamentaria(Exercicio exercicio, List<HierarquiaOrganizacional> listaUnidades) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }
        try {
            String hql = "select est.* from estornoalteracaoorc est "
                + "   inner join exercicio ex on est.exercicio_id = ex.id "
                + "   where ex.id = :ex" +
                "       and est.unidadeOrganizacionalOrc_id in (:unidades) ";
            Query consulta = em.createNativeQuery(hql, EstornoAlteracaoOrc.class);
            consulta.setParameter("ex", exercicio);
            consulta.setParameter("unidades", unidades);
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        List<ConsultaMovimentoContabil> retorno = Lists.newArrayList();
        retorno.add(criarConsultaSuplementacao(filtros));
        retorno.add(criarConsultaAnulacao(filtros));
        retorno.add(criarConsultaReceitaAlteracao(filtros));
        return retorno;
    }

    public ConsultaMovimentoContabil criarConsultaSuplementacao(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(EstornoSuplementacaoOrc.class, ConsultaMovimentoContabil.clonarFiltros(filtros));
        consulta.incluirJoinsComplementar(" inner join estornoAlteracaoOrc alt on obj.estornoAlteracaoOrc_id = alt.id");
        consulta.incluirJoinsComplementar(" inner join suplementacaoORC supl on obj.suplementacaoORC_id = supl.id");
        consulta.incluirJoinsOrcamentoDespesa(" supl.fonteDespesaORC_id ");
        alterarFiltrosComuns(consulta);
        return consulta;
    }

    public ConsultaMovimentoContabil criarConsultaAnulacao(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(EstornoAnulacaoOrc.class, ConsultaMovimentoContabil.clonarFiltros(filtros));
        consulta.incluirJoinsComplementar(" inner join estornoAlteracaoOrc alt on obj.estornoAlteracaoOrc_id = alt.id");
        consulta.incluirJoinsComplementar(" inner join anulacaoORC anul on obj.anulacaoORC_id = anul.id");
        consulta.incluirJoinsOrcamentoDespesa(" anul.fonteDespesaORC_id ");
        alterarFiltrosComuns(consulta);
        return consulta;
    }

    public void alterarFiltrosComuns(ConsultaMovimentoContabil consulta) {
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(alt.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(alt.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "prov.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "prov.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "alt.codigo");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
    }

    public ConsultaMovimentoContabil criarConsultaReceitaAlteracao(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(EstornoReceitaAlteracaoOrc.class, ConsultaMovimentoContabil.clonarFiltros(filtros));
        consulta.incluirJoinsComplementar(" inner join receitaAlteracaoORC recOrc on obj.receitaAlteracaoORC_id = recOrc.id");
        consulta.incluirJoinsComplementar(" inner join estornoAlteracaoOrc alt on obj.estornoAlteracaoOrc_id = alt.id");
        consulta.incluirJoinsComplementar(" inner join receitaloafonte rcfonte on recOrc.receitaloa_id = rcfonte.receitaloa_id");
        consulta.incluirJoinsOrcamentoReceita("recOrc.receitaloa_id", "rcfonte.id");
        return consulta;
    }
}

