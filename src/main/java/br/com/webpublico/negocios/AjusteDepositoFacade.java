/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author claudio
 */
@Stateless
public class AjusteDepositoFacade extends SuperFacadeContabil<AjusteDeposito> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ContaFacade contaExtraorcamentariaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private SaldoExtraorcamentarioFacade saldoExtraorcamentarioFacade;
    @EJB
    private ConfigAjusteDepositoFacade configAjusteDepositoFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    @EJB
    private SubContaFacade subContaFacade;

    public AjusteDepositoFacade() {
        super(AjusteDeposito.class);
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public ContaFacade getContaExtraorcamentariaFacade() {
        return contaExtraorcamentariaFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public SaldoExtraorcamentarioFacade getSaldoExtraorcamentarioFacade() {
        return saldoExtraorcamentarioFacade;
    }

    public void setSaldoExtraorcamentarioFacade(SaldoExtraorcamentarioFacade saldoExtraorcamentarioFacade) {
        this.saldoExtraorcamentarioFacade = saldoExtraorcamentarioFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public AjusteDeposito recuperar(Object id) {
        AjusteDeposito obj = super.recuperar(id);
        obj.getReceitasExtras().size();
        return obj;
    }

    public void salvarNovo(AjusteDeposito entity, SubConta contaFinanceira) throws ExcecaoNegocioGenerica {
        try {
            if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataAjuste());
                entity.setSituacao(SituacaoAjusteDeposito.EFETUADO);
                if (entity.getId() == null) {
                    entity.gerarHistoricos();
                    entity.setSaldo(entity.getValor());
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroAjusteDeposito(entity.getUnidadeOrganizacional(), entity.getDataAjuste()));
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                movimentarReceitaExtra(entity, contaFinanceira);
                gerarSaldoContaExtra(entity);
                contabilizarAjuste(entity);
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    @Override
    public void salvar(AjusteDeposito entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    private void contabilizarAjuste(AjusteDeposito entity) throws ExcecaoNegocioGenerica {

        EventoContabil eventoContabil = recuperarEventoContabil(entity);
        Preconditions.checkNotNull(eventoContabil, "Evento contábil não encontrado.");
        entity.setEventoContabil(eventoContabil);
        entity.gerarHistoricos();
        ParametroEvento parametroEvento = criarParametroEvento(entity);
        parametroEvento.setTipoBalancete(entity.getEventoContabil().getTipoBalancete());
        ItemParametroEvento item = criarItemParametroEvento(entity, parametroEvento);
        item.setOperacaoClasseCredor(classeCredorFacade.recuperaOperacaoAndVigenciaClasseCredor(entity.getPessoa(), entity.getClasseCredor(), entity.getDataAjuste()));
        item.setObjetoParametros(criarObjetosParametros(entity, item));
        parametroEvento.getItensParametrosEvento().add(item);
        contabilizacaoFacade.contabilizar(parametroEvento);
    }

    private void gerarSaldoContaExtra(AjusteDeposito selecionado) {

        if (selecionado.isAjusteDiminutivo()) {
            gerarSaldoExtraPorTipoOperacao(selecionado, TipoOperacao.CREDITO);
        } else {
            if (validarSaldoContaExtra(selecionado)) {
                gerarSaldoExtraPorTipoOperacao(selecionado, TipoOperacao.DEBITO);
            }
        }
    }

    private void gerarSaldoExtraPorTipoOperacao(AjusteDeposito selecionado, TipoOperacao tipoOperacao) {
        saldoExtraorcamentarioFacade.gerarSaldoExtraorcamentario(
            selecionado.getDataAjuste(),
            tipoOperacao,
            selecionado.getValor(),
            selecionado.getContaExtraorcamentaria(),
            selecionado.getContaDeDestinacao(),
            selecionado.getUnidadeOrganizacional());
    }

    private Boolean validarSaldoContaExtra(AjusteDeposito selecionado) {
        SaldoExtraorcamentario saldoExtraorcamentario = saldoExtraorcamentarioFacade.recuperaUltimoSaldoPorData(selecionado.getDataAjuste(), selecionado.getContaExtraorcamentaria(), selecionado.getContaDeDestinacao(), selecionado.getUnidadeOrganizacional());
        BigDecimal valor = saldoExtraorcamentario.getValor().subtract(selecionado.getValor());
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ExcecaoNegocioGenerica(" O Saldo da Conta Extraorçamentária: " + selecionado.getContaExtraorcamentaria() + ", ficará negativo em " + "<b>" + Util.formataValor(valor) + "</b>" + " na data " + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + ".");
        }
        return true;
    }

    private ReceitaExtra criarObjetoReceitaExtra(AjusteDeposito entity, SubConta contaFinanceira) {
        ReceitaExtra novaReceita = new ReceitaExtra();
        novaReceita.setExercicio(sistemaFacade.getExercicioCorrente());
        novaReceita.setDataReceita(entity.getDataAjuste());
        novaReceita.setValor(entity.getValor());
        novaReceita.setSaldo(entity.getSaldo());
        novaReceita.setValorEstornado(BigDecimal.ZERO);
        novaReceita.setComplementoHistorico(getHistorico(entity));
        novaReceita.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        novaReceita.setUnidadeOrganizacionalAdm(entity.getUnidadeOrganizacionalAdm());
        novaReceita.setContaExtraorcamentaria(entity.getContaExtraorcamentaria());
        novaReceita.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        novaReceita.setSubConta(contaFinanceira);
        novaReceita.setPessoa(entity.getPessoa());
        novaReceita.setClasseCredor(entity.getClasseCredor());
        novaReceita.setFonteDeRecursos(getFonteDeRecursosVigente(entity.getFonteDeRecurso()));
        novaReceita.setSituacaoReceitaExtra(SituacaoReceitaExtra.ABERTO);
        novaReceita.setNumero(getNumeroReceitaExtra(novaReceita));
        novaReceita.setTransportado(Boolean.FALSE);
        novaReceita.setReceitaDeAjusteDeposito(Boolean.TRUE);
        novaReceita = receitaExtraFacade.salvarRetornando(novaReceita);
        return novaReceita;
    }

    private String getHistorico(AjusteDeposito entity) {
        return "Ajuste " + entity.getTipoAjuste().getDescricao() + ", N° " + entity.getNumero() + "/" + DataUtil.getDataFormatada(entity.getDataAjuste()) + ", " + entity.getHistorico();
    }

    private String getNumeroReceitaExtra(ReceitaExtra novaReceita) {
        return receitaExtraFacade.getSingletonGeradorCodigoContabil().getNumeroReceitaExtra(sistemaFacade.getExercicioCorrente(), novaReceita.getUnidadeOrganizacional(), novaReceita.getDataReceita());
    }

    private FonteDeRecursos getFonteDeRecursosVigente(FonteDeRecursos fonteOriginal) {
        try {
            FonteDeRecursos fonteRecuperada = receitaExtraFacade.getFonteDeRecursosFacade().recuperaPorCodigoExercicio(fonteOriginal, sistemaFacade.getExercicioCorrente());
            validarFonteRecursoVigente(fonteRecuperada, fonteOriginal);
            return fonteRecuperada;
        } catch (ValidacaoException ex) {
            throw ex;
        }
    }

    private void validarFonteRecursoVigente(FonteDeRecursos fonteRecuperada, FonteDeRecursos fonteOriginal) {
        ValidacaoException ve = new ValidacaoException();
        if (fonteRecuperada == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A Fonte de Recurso" + fonteOriginal + " não possui uma fonte vigente para o exercício de " + sistemaFacade.getExercicioCorrente());
        }
        ve.lancarException();
    }

    private void movimentarReceitaExtra(AjusteDeposito entity, SubConta contaFinanceira) {
        if (entity.isAjusteDiminutivo()) {
            ReceitaExtra receitaExtra = criarObjetoReceitaExtra(entity, contaFinanceira);
            AjusteDepositoReceitaExtra ajusteReceita = new AjusteDepositoReceitaExtra();
            ajusteReceita.setReceitaExtra(receitaExtra);
            ajusteReceita.setAjusteDeposito(entity);
            entity.getReceitasExtras().add(ajusteReceita);
        } else {
            for (AjusteDepositoReceitaExtra ajusteReceita : entity.getReceitasExtras()) {
                ReceitaExtra receitaExtra = receitaExtraFacade.recuperar(ajusteReceita.getReceitaExtra().getId());
                receitaExtra.setSaldo(BigDecimal.ZERO);
                receitaExtra.setSituacaoReceitaExtra(SituacaoReceitaExtra.EFETUADO);
                em.merge(receitaExtra);
            }
        }
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public SingletonGeradorCodigoContabil getSingletonGeradorCodigoContabil() {
        return singletonGeradorCodigoContabil;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarAjuste((AjusteDeposito) entidadeContabil);
    }

    public List<AjusteDeposito> recuperarAjusteDepositoDaPessoa(Pessoa pessoa) {
        String sql = " select aj.* from ajustedeposito aj   " +
            "       where aj.pessoa_id = :pessoa        ";
        Query q = em.createNativeQuery(sql, AjusteDeposito.class);
        q.setParameter("pessoa", pessoa.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public EventoContabil recuperarEventoContabil(AjusteDeposito entity) {
        EventoContabil eventoContabil = null;
        ConfigAjusteDeposito configAjusteDeposito = configAjusteDepositoFacade.buscarConfiguracaoCDE(entity, TipoLancamento.NORMAL);
        if (configAjusteDeposito != null && configAjusteDeposito.getEventoContabil() != null) {
            eventoContabil = configAjusteDeposito.getEventoContabil();
        }
        return eventoContabil;
    }

    public ParametroEvento getParametroEvento(EntidadeContabil entidadeContabil) {
        try {
            AjusteDeposito entity = (AjusteDeposito) entidadeContabil;
            ParametroEvento parametroEvento = criarParametroEvento(entity);
            ItemParametroEvento item = criarItemParametroEvento(entity, parametroEvento);
            List<ObjetoParametro> objetos = criarObjetosParametros(entity, item);
            item.setObjetoParametros(objetos);
            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    private ParametroEvento criarParametroEvento(AjusteDeposito entity) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataAjuste());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
        parametroEvento.setIdOrigem(entity.getId() == null ? null : entity.getId().toString());
        parametroEvento.setExercicio(entity.getFonteDeRecurso().getExercicio());
        return parametroEvento;
    }

    private ItemParametroEvento criarItemParametroEvento(AjusteDeposito entity, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(entity.getValor());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    private List<ObjetoParametro> criarObjetosParametros(AjusteDeposito entity, ItemParametroEvento item) {
        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(entity.getId().toString(), AjusteDeposito.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(entity.getContaExtraorcamentaria().getId().toString(), ContaExtraorcamentaria.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(entity.getClasseCredor().getId().toString(), ClasseCredor.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(entity.getFonteDeRecurso().getId().toString(), FonteDeRecursos.class.getSimpleName(), item));
        return objetos;
    }

    public List<AjusteDeposito> buscarAjusteDepositoPorUnidade(String parte, UnidadeOrganizacional unidade) {

        String sql = " select aj.* " +
            "   from ajustedeposito aj " +
            "   inner join pessoa p on p.id = aj.pessoa_id " +
            "   left join pessoafisica pf on pf.id = p.id " +
            "   left join pessoajuridica pj on pj.id = p.id " +
            "   where aj.unidadeorganizacional_id = :idUnidade " +
            "    and aj.saldo > 0 " +
            "    and EXTRACT (year from aj.dataajuste) = :exercicio" +
            "    and (aj.numero like :parte "
            + "   or lower(pj.razaoSocial) like :parte or pj.cnpj like :parte "
            + "   or lower(pf.nome) like :parte or pf.cpf like :parte ) "
            + " order by aj.dataajuste desc";
        Query q = em.createNativeQuery(sql, AjusteDeposito.class);
        q.setParameter("idUnidade", unidade);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("exercicio", sistemaFacade.getExercicioCorrente().getAno());
        q.setMaxResults(30);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<ReceitaExtra> buscarReceitaExtraPorAjuste(AjusteDeposito ajusteDeposito) {
        String sql = " select rex.* from receitaextra rex   " +
            "       inner join ajustedepositoreceitaextra ajrex on ajrex.receitaextra_id = rex.id " +
            "       where ajrex.ajustedeposito_id = :idAjuste        ";
        Query q = em.createNativeQuery(sql, ReceitaExtra.class);
        q.setParameter("idAjuste", ajusteDeposito.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ReceitaExtraFacade getReceitaExtraFacade() {
        return receitaExtraFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(AjusteDeposito.class, filtros);
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataAjuste)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataAjuste)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "obj.fonteDeRecursos_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_EXTRA, "obj.contaExtraorcamentaria_id");
        return Arrays.asList(consulta);
    }
}
