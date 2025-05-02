/*
 * Codigo gerado automaticamente em Mon Sep 17 16:20:52 GMT-03:00 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.EstornoLiberacaoCotaPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.financeiro.SuperEntidadeContabilFinanceira;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class EstornoLibCotaFinanceiraFacade extends SuperFacadeContabil<EstornoLibCotaFinanceira> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private LiberacaoCotaFinanceiraFacade liberacaoCotaFinanceiraFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfigLiberacaoFinanceiraFacade configLiberacaoFinanceiraFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;


    public EstornoLibCotaFinanceiraFacade() {
        super(EstornoLibCotaFinanceira.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public ContabilizacaoFacade getContabilizacaoFacade() {
        return contabilizacaoFacade;
    }

    public ConfigLiberacaoFinanceiraFacade getConfigLiberacaoFinanceiraFacade() {
        return configLiberacaoFinanceiraFacade;
    }

    public LiberacaoCotaFinanceiraFacade getLiberacaoCotaFinanceiraFacade() {
        return liberacaoCotaFinanceiraFacade;
    }

    public List<EstornoLibCotaFinanceira> listaPorUnidade(UnidadeOrganizacional uo) {
        String sql = "SELECT * FROM ESTORNOLIBCOTAFINANCEIRA WHERE UNIDADEORGANIZACIONAL_ID =:uo ORDER BY ID DESC";
        Query q = em.createNativeQuery(sql, EstornoLibCotaFinanceira.class);
        q.setParameter("uo", uo.getId());
        return q.getResultList();
    }

    public EstornoLibCotaFinanceira salvarRetornando(EstornoLibCotaFinanceira entity) {
        if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
            throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        } else {
            validarConcorrencia(entity);
            singletonConcorrenciaContabil.bloquear(entity.getLiberacao());
            hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataEstorno());
            movimentarLiberacaoFinanceira(entity);
            if (entity.getId() == null) {
                entity.setNumero(singletonGeradorCodigoContabil.getNumeroLiberacaoFinanciera(entity.getLiberacao().getExercicio(), entity.getLiberacao().getUnidadeOrganizacional(), entity.getDataEstorno()));
            }
            entity.gerarHistoricos();
            entity = em.merge(entity);
            geraSaldoContaFinanceira(entity);
            contabilizaEstornoLiberacaoRecebido(entity);
            contabilizaEstornoLiberacaoConcedido(entity);
            portalTransparenciaNovoFacade.salvarPortal(new EstornoLiberacaoCotaPortal(entity));
        }
        return entity;
    }

    public void desbloquearLiberacao(EstornoLibCotaFinanceira entity) {
        singletonConcorrenciaContabil.desbloquear(entity.getLiberacao());
    }

    private void validarConcorrencia(EstornoLibCotaFinanceira entity) {
        ValidacaoException ve = new ValidacaoException();
        if (!singletonConcorrenciaContabil.isDisponivel(entity.getLiberacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A liberação financeira: " + entity.getLiberacao().getNumero() + " está sendo utilizado por outro usuário. Caso o problema persistir, selecione novamente a liberação.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }


    private void geraSaldoContaFinanceira(EstornoLibCotaFinanceira entity) {

        saldoSubContaFacade.gerarSaldoFinanceiro(entity.getLiberacao().getSolicitacaoCotaFinanceira().getUnidadeOrganizacional(),
            entity.getLiberacao().getSolicitacaoCotaFinanceira().getContaFinanceira(),
            entity.getLiberacao().getSolicitacaoCotaFinanceira().getContaDeDestinacao(),
            entity.getValor(),
            TipoOperacao.DEBITO,
            entity.getDataEstorno(),
            entity.getEventoContabilRetirada(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.ESTORNO_LIBERACAO_SALDO_CONTA_FINANCEIRA,
            entity.getUuid(),
            true);

        saldoSubContaFacade.gerarSaldoFinanceiro(entity.getLiberacao().getUnidadeOrganizacional(),
            entity.getLiberacao().getSubConta(),
            entity.getLiberacao().getContaDeDestinacao(),
            entity.getValor(), TipoOperacao.CREDITO,
            entity.getDataEstorno(),
            entity.getEventoContabilDeposito(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.ESTORNO_LIBERACAO_SALDO_CONTA_FINANCEIRA,
            entity.getUuid(),
            true);
    }

    private void movimentarLiberacaoFinanceira(EstornoLibCotaFinanceira entity) {
        LiberacaoCotaFinanceira liberacao = entity.getLiberacao();
        liberacao.setSaldo(liberacao.getSaldo().subtract(entity.getValor()));
        if (liberacao.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            liberacao.setStatusPagamento(StatusPagamento.ESTORNADO);
            if (liberacao.getDataConciliacao() == null) {
                liberacao.setDataConciliacao(sistemaFacade.getDataOperacao());
            }
            if (liberacao.getRecebida() == null) {
                liberacao.setRecebida(sistemaFacade.getDataOperacao());
            }
            entity.setDataConciliacao(sistemaFacade.getDataOperacao());
            entity.setRecebida(sistemaFacade.getDataOperacao());
        }
        if (liberacao.getContaFinanceiraRecebida().getContaBancariaEntidade().equals(liberacao.getContaFinanceiraRetirada().getContaBancariaEntidade())) {
            entity.setDataConciliacao(sistemaFacade.getDataOperacao());
            entity.setRecebida(sistemaFacade.getDataOperacao());
        }
        em.merge(liberacao);
    }

    @Override
    public void salvar(EstornoLibCotaFinanceira entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    public String getUltimoNumero() {
        String sql = "SELECT MAX(TO_NUMBER(CR.NUMERO))+1 AS ULTIMONUMERO FROM ESTORNOLIBCOTAFINANCEIRA CR";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void contabilizaEstornoLiberacaoRecebido(EstornoLibCotaFinanceira entity) throws ExcecaoNegocioGenerica {

        ConfigLiberacaoFinanceira configLiberacaoFinanceira = configLiberacaoFinanceiraFacade.recuperaEvento(TipoLancamento.ESTORNO, entity.getLiberacao().getTipoLiberacaoFinanceira(), OrigemTipoTransferencia.RECEBIDO, entity.getDataEstorno(), entity.getLiberacao().getSolicitacaoCotaFinanceira().getResultanteIndependente());
        if (configLiberacaoFinanceira != null && configLiberacaoFinanceira.getEventoContabil() != null) {
            entity.setEventoContabilDeposito(configLiberacaoFinanceira.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização do Estorno da Liberação Financeira (Recebido).");
        }

        if (entity.getEventoContabilDeposito() != null) {
            entity.gerarHistoricos();
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setDataEvento(entity.getDataEstorno());
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setUnidadeOrganizacional(entity.getLiberacao().getUnidadeRecebida());
            parametroEvento.setExercicio(entity.getLiberacao().getExercicio());
            parametroEvento.setEventoContabil(entity.getEventoContabilDeposito());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.RECEBIDO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity, item));
            objetos.add(new ObjetoParametro(entity.getLiberacao().getContaFinanceiraRecebida(), item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada.");
        }
    }

    public void contabilizaEstornoLiberacaoConcedido(EstornoLibCotaFinanceira entity) throws ExcecaoNegocioGenerica {

        ConfigLiberacaoFinanceira configLiberacaoFinanceira = configLiberacaoFinanceiraFacade.recuperaEvento(TipoLancamento.ESTORNO, entity.getLiberacao().getTipoLiberacaoFinanceira(), OrigemTipoTransferencia.CONCEDIDO, entity.getDataEstorno(), entity.getLiberacao().getSolicitacaoCotaFinanceira().getResultanteIndependente());
        if (configLiberacaoFinanceira != null && configLiberacaoFinanceira.getEventoContabil() != null) {
            entity.setEventoContabilRetirada(configLiberacaoFinanceira.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização do Estorno da Liberação Financeira (Concedido).");
        }

        if (entity.getEventoContabilRetirada() != null) {
            entity.gerarHistoricos();
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setDataEvento(entity.getDataEstorno());
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setUnidadeOrganizacional(entity.getLiberacao().getUnidadeRetirada());
            parametroEvento.setExercicio(entity.getLiberacao().getExercicio());
            parametroEvento.setEventoContabil(entity.getEventoContabilRetirada());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.CONCEDIDO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity, item));
            objetos.add(new ObjetoParametro(entity.getLiberacao().getContaFinanceiraRetirada(), item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada.");
        }
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public SingletonGeradorCodigoContabil getSingletonGeradorCodigoContabil() {
        return singletonGeradorCodigoContabil;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        ParametroEvento.ComplementoId complementoId = ((SuperEntidadeContabilFinanceira) entidadeContabil).getComplementoId();
        if (complementoId.equals(ParametroEvento.ComplementoId.NORMAL)
            || complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            contabilizaEstornoLiberacaoConcedido((EstornoLibCotaFinanceira) entidadeContabil);
        }
        if (complementoId.equals(ParametroEvento.ComplementoId.NORMAL)
            || complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            contabilizaEstornoLiberacaoRecebido((EstornoLibCotaFinanceira) entidadeContabil);
        }
    }

    public void estornarConciliacao(EstornoLibCotaFinanceira estornoLibCotaFinanceira) {
        try {
            estornoLibCotaFinanceira.setDataConciliacao(null);
            estornoLibCotaFinanceira.setRecebida(null);
            getEntityManager().merge(estornoLibCotaFinanceira);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a conciliação. Consulte o suporte!");
        }
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    public List<EstornoLibCotaFinanceira> listaEstornoLiberacaoPorUnidadesExercicio(Exercicio ano, List<HierarquiaOrganizacional> listaUnidades) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }

        String hql = " select est from EstornoLibCotaFinanceira est" +
            " where est.liberacao.solicitacaoCotaFinanceira.unidadeOrganizacional in (:unidades)" +
            " and est.liberacao.exercicio.id = :ano";
        Query consulta = em.createQuery(hql, EstornoLibCotaFinanceira.class);
        consulta.setParameter("unidades", unidades);
        consulta.setParameter("ano", ano.getId());
        List resultList = consulta.getResultList();
        if (resultList.isEmpty()) {
            return new ArrayList<>();
        } else {
            return resultList;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public List<EstornoLibCotaFinanceira> recuperarObjetosReprocessamento(EventosReprocessar er, Boolean isReprocessamentoInicial, ParametroEvento.ComplementoId complementoId) {

        String sql = "select est.* from EstornoLibCotaFinanceira est " +
            " inner join LiberacaoCotaFinanceira lib on est.liberacao_id = lib.id" +
            " INNER JOIN SOLICITACAOCOTAFINANCEIRA SCF ON lib.SOLICITACAOCOTAFINANCEIRA_ID = SCF.ID " +
            " where trunc(est.dataEstorno) between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')";
        if (er.hasUnidades() && complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            sql = sql + " and lib.unidadeOrganizacional_id in (:unidades) ";
        }
        if (er.hasUnidades() && complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            sql = sql + " and SCF.unidadeOrganizacional_id in (:unidades) ";
        }
        if (!isReprocessamentoInicial && complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            sql += " and lib.eventoContabilRetirada_id = :evento";
        }
        if (!isReprocessamentoInicial && complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            sql += " and lib.eventoContabilDeposito_id = :evento";
        }

        sql += " order by trunc(est.dataEstorno) asc";

        Query q = em.createNativeQuery(sql, EstornoLibCotaFinanceira.class);
        try {
            q.setParameter("di", DataUtil.getDataFormatada(er.getDataInicial()));
            q.setParameter("df", DataUtil.getDataFormatada(er.getDataFinal()));
            if (er.hasUnidades()) {
                q.setParameter("unidades", er.getIdUnidades());
            }
            if (!isReprocessamentoInicial) {
                q.setParameter("evento", er.getEventoContabil());
            }
            List<EstornoLibCotaFinanceira> retorno = q.getResultList();
            for (EstornoLibCotaFinanceira entidade : retorno) {
                entidade.setComplementoId(complementoId);
            }
            return retorno;
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(EstornoLibCotaFinanceira.class, filtros);
        consulta.incluirJoinsComplementar(" inner join LiberacaoCotaFinanceira liberacao on obj.liberacao_id = liberacao.id");
        consulta.incluirJoinsComplementar(" inner join solicitacaoCotaFinanceira sol on liberacao.solicitacaoCotaFinanceira_id = sol.id ");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabilRetirada_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabildeposito_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "sol.fonteDeRecursos_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "liberacao.fonteDeRecursos_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "liberacao.subConta_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "sol.contafinanceira_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        return Arrays.asList(consulta);
    }
}
