/*
 * Codigo gerado automaticamente em Thu Sep 27 09:50:10 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
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

@Stateless
public class AjusteAtivoDisponivelFacade extends SuperFacadeContabil<AjusteAtivoDisponivel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ConfigAjusteDisponivelFacade configAjusteDisponivelFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private ContaFacade contaFacade;

    public AjusteAtivoDisponivelFacade() {
        super(AjusteAtivoDisponivel.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public ConfigAjusteDisponivelFacade getConfigAjusteDisponivelFacade() {
        return configAjusteDisponivelFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public String getUltimoNumero() {
        String sql = "SELECT MAX(TO_NUMBER(CR.NUMERO))+1 AS ULTIMONUMERO FROM AJUSTEATIVODISPONIVEL CR";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }

        return q.getSingleResult().toString();
    }

    public void gerarSaldoContaFinanceira(AjusteAtivoDisponivel entity) {
        if (entity.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            if (TipoAjusteDisponivel.retornarAumentativo().contains(entity.getTipoAjusteDisponivel())) {
                gerarSaldoSubConta(entity, TipoOperacao.CREDITO, MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_NORMAL);
            } else {
                gerarSaldoSubConta(entity, TipoOperacao.DEBITO, MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_NORMAL);
            }
        } else {
            if (TipoAjusteDisponivel.retornarAumentativo().contains(entity.getTipoAjusteDisponivel())) {
                gerarSaldoSubConta(entity, TipoOperacao.DEBITO, MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_ESTORNO);
            } else {
                gerarSaldoSubConta(entity, TipoOperacao.CREDITO, MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_ESTORNO);
            }
        }
    }

    public void gerarSaldoSubConta(AjusteAtivoDisponivel ajusteAtivoDisponivel, TipoOperacao tipoOperacao, MovimentacaoFinanceira movimentacaoFinanceira) {
        saldoSubContaFacade.gerarSaldoFinanceiro(ajusteAtivoDisponivel.getUnidadeOrganizacional(),
            ajusteAtivoDisponivel.getContaFinanceira(),
            ajusteAtivoDisponivel.getContaDeDestinacao(),
            ajusteAtivoDisponivel.getValor(), tipoOperacao,
            ajusteAtivoDisponivel.getDataAjuste(),
            ajusteAtivoDisponivel.getEventoContabil(),
            ajusteAtivoDisponivel.getHistoricoRazao(),
            movimentacaoFinanceira,
            ajusteAtivoDisponivel.getUuid(),
            true);
    }

    @Override
    public void salvarNovo(AjusteAtivoDisponivel entity) {
        try {
            if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataAjuste());
                if (TipoLancamento.NORMAL.equals(entity.getTipoLancamento())) {
                    entity.setSaldo(entity.getValor());
                }
                if (entity.getId() == null) {
                    entity.setNumero(getUltimoNumero());
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                gerarSaldoContaFinanceira(entity);
                contabilizarAjuste(entity);
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    @Override
    public void salvar(AjusteAtivoDisponivel entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    public BigDecimal valorSaldoAjuste(AjusteAtivoDisponivel ajusteAtivoDisponivel) {
        String sql = "SELECT COALESCE(SUM(VALOR),0) "
            + " FROM AJUSTEATIVODISPONIVEL "
            + " WHERE TIPOLANCAMENTO = 'NORMAL' "
            + " AND TIPOAJUSTEDISPONIVEL = :tipoajuste "
            + " AND UNIDADEORGANIZACIONAL_ID = :unidade "
            + " AND CONTAFINANCEIRA_ID = :subConta "
            + " AND FONTEDERECURSOS_ID = :fonte "
            + " AND trunc(DATAAJUSTE) <= to_date(:data, 'dd/mm/yyyy')";
        Query q = em.createNativeQuery(sql);

        q.setParameter("tipoajuste", ajusteAtivoDisponivel.getTipoAjusteDisponivel().name());
        q.setParameter("unidade", ajusteAtivoDisponivel.getUnidadeOrganizacional().getId());
        q.setParameter("subConta", ajusteAtivoDisponivel.getContaFinanceira().getId());
        q.setParameter("fonte", ajusteAtivoDisponivel.getFonteDeRecursos().getId());
        q.setParameter("data", DataUtil.getDataFormatada(ajusteAtivoDisponivel.getDataAjuste()));
        BigDecimal valorNormal = BigDecimal.ZERO;
        if (q.getResultList() != null || !q.getResultList().isEmpty()) {
            valorNormal = (BigDecimal) q.getSingleResult();
        }

        String sqle = " SELECT COALESCE(SUM(VALOR),0) "
            + " FROM AJUSTEATIVODISPONIVEL "
            + " WHERE TIPOLANCAMENTO = 'ESTORNO' "
            + " AND TIPOAJUSTEDISPONIVEL = :tipoajuste"
            + " AND UNIDADEORGANIZACIONAL_ID = :unidade"
            + " AND CONTAFINANCEIRA_ID = :subConta"
            + " AND FONTEDERECURSOS_ID = :fonte "
            + " AND trunc(DATAAJUSTE) <= to_date(:data, 'dd/mm/yyyy')";
        Query q2 = em.createNativeQuery(sqle);
        q2.setParameter("tipoajuste", ajusteAtivoDisponivel.getTipoAjusteDisponivel().name());
        q2.setParameter("unidade", ajusteAtivoDisponivel.getUnidadeOrganizacional().getId());
        q2.setParameter("subConta", ajusteAtivoDisponivel.getContaFinanceira().getId());
        q2.setParameter("fonte", ajusteAtivoDisponivel.getFonteDeRecursos().getId());
        q2.setParameter("data", DataUtil.getDataFormatada(ajusteAtivoDisponivel.getDataAjuste()));
        BigDecimal valorEstorno = BigDecimal.ZERO;
        if (q2.getResultList() != null || !q2.getResultList().isEmpty()) {
            valorEstorno = (BigDecimal) q2.getSingleResult();
        }
        return valorNormal.subtract(valorEstorno);
    }

    public void contabilizarAjuste(AjusteAtivoDisponivel entity) {

        ConfigAjusteAtivoDisponivel configAjusteAtivoDisponivel = configAjusteDisponivelFacade.getUltimaConfiguracao(entity);
        if (configAjusteAtivoDisponivel != null && configAjusteAtivoDisponivel.getEventoContabil() != null) {
            entity.setEventoContabil(configAjusteAtivoDisponivel.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Ajuste Ativo Dispońivel.");
        }
        entity.gerarHistoricos();
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataAjuste());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setExercicio(entity.getFonteDeRecursos().getExercicio());
        parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
        parametroEvento.setIdOrigem(entity.getId().toString());
        parametroEvento.setEventoContabil(entity.getEventoContabil());

        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(entity.getValor());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        item.setOperacaoClasseCredor(classeCredorFacade.recuperaOperacaoAndVigenciaClasseCredor(entity.getPessoa(), entity.getClasseCredor(), entity.getDataAjuste()));

        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(entity, item));
        objetos.add(new ObjetoParametro(entity.getContaFinanceira(), item));
        objetos.add(new ObjetoParametro(entity.getClasseCredor(), item));
        objetos.add(new ObjetoParametro(entity.getFonteDeRecursos(), item));
        item.setObjetoParametros(objetos);


        parametroEvento.getItensParametrosEvento().add(item);

        contabilizacaoFacade.contabilizar(parametroEvento);
    }


    public void estornarConciliacao(AjusteAtivoDisponivel ajusteAtivoDisponivel) {
        try {
            ajusteAtivoDisponivel.setDataConciliacao(null);
            getEntityManager().merge(ajusteAtivoDisponivel);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a conciliação. Consulte o suporte!");
        }
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarAjuste((AjusteAtivoDisponivel) entidadeContabil);
    }

    public List<AjusteAtivoDisponivel> recuperarAjusteAtivoDisponivelDaPessoa(Pessoa pessoa) {
        String sql = " select aj.* from ajusteativodisponivel aj   " +
            "       where aj.pessoa_id = :pessoa        ";
        Query q = em.createNativeQuery(sql, AjusteAtivoDisponivel.class);
        q.setParameter("pessoa", pessoa.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(AjusteAtivoDisponivel.class, filtros);
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataAjuste)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataAjuste)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "obj.fonteDeRecursos_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "obj.contaFinanceira_id");
        return Arrays.asList(consulta);
    }
}
