/*
 * Codigo gerado automaticamente em Mon Sep 17 16:20:52 GMT-03:00 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.EstornoTransferenciaPortal;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class EstornoTransferenciaFacade extends SuperFacadeContabil<EstornoTransferencia> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private TransferenciaContaFinanceiraFacade transferenciaContaFinanceiraFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfiguracaoTranferenciaFinanceiraFacade configuracaoTranferenciaFinanceiraFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;

    public EstornoTransferenciaFacade() {
        super(EstornoTransferencia.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public TransferenciaContaFinanceiraFacade getTransferenciaContaFinanceiraFacade() {
        return transferenciaContaFinanceiraFacade;
    }

    public List<EstornoTransferencia> listaPorUnidade(UnidadeOrganizacional uo) {
        String sql = "SELECT * FROM ESTORNOTRANSFERENCIA WHERE UNIDADEORGANIZACIONAL_ID =:uo ORDER BY ID DESC";
        Query q = em.createNativeQuery(sql, EstornoTransferencia.class);
        q.setParameter("uo", uo.getId());
        return q.getResultList();
    }

    @Override
    public void salvarNovo(EstornoTransferencia entity) {
        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                validarConcorrencia(entity);
                singletonConcorrenciaContabil.bloquear(entity.getTransferencia());
                if (entity.getId() == null) {
                    movimentarEstornoTransferencia(entity);
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroTranferenciaContaFinanceira(entity.getExercicio(), entity.getTransferencia().getUnidadeOrgConcedida(), entity.getDataEstorno()));
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                gerarSaldoContaFinanceiraConcedida(entity);
                gerarSaldoContaFinanceiraRecebida(entity);

                contabilizarTransferenciaDeposito(entity);
                contabilizarTransferenciaRetirada(entity);
                singletonConcorrenciaContabil.desbloquear(entity.getTransferencia());
                portalTransparenciaNovoFacade.salvarPortal(new EstornoTransferenciaPortal(entity));
            }
        } catch (ValidacaoException ve) {
            throw ve;
        } catch (Exception ex) {
            singletonConcorrenciaContabil.desbloquear(entity.getTransferencia());
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void validarConcorrencia(EstornoTransferencia entity) {
        ValidacaoException ve = new ValidacaoException();
        if (!singletonConcorrenciaContabil.isDisponivel(entity.getTransferencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Transferência: " + entity.getTransferencia().getNumero() + " está sendo utilizado por outro usuário. Caso o problema persistir, selecione novamente a transferência.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void gerarSaldoContaFinanceiraConcedida(EstornoTransferencia entity) {
        saldoSubContaFacade.gerarSaldoFinanceiro(entity.getTransferencia().getUnidadeOrgConcedida(),
            entity.getTransferencia().getSubContaRetirada(),
            entity.getTransferencia().getContaDeDestinacaoRetirada(),
            entity.getValor(),
            TipoOperacao.CREDITO,
            entity.getDataEstorno(),
            entity.getEventoContabilRetirada(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.ESTORNO_TRANSFERENCIA_SALDO_CONTA_FINANCEIRA,
            entity.getUuid(),
            true);
    }

    private void gerarSaldoContaFinanceiraRecebida(EstornoTransferencia entity) {
        saldoSubContaFacade.gerarSaldoFinanceiro(entity.getTransferencia().getUnidadeOrganizacional(),
            entity.getTransferencia().getSubContaDeposito(),
            entity.getTransferencia().getContaDeDestinacaoDeposito(),
            entity.getValor(),
            TipoOperacao.DEBITO,
            entity.getDataEstorno(),
            entity.getEventoContabil(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.ESTORNO_TRANSFERENCIA_SALDO_CONTA_FINANCEIRA,
            entity.getUuid(),
            true);
    }

    private void movimentarEstornoTransferencia(EstornoTransferencia entity) {
        hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataEstorno());
        TransferenciaContaFinanceira transferencia = transferenciaContaFinanceiraFacade.recuperar(entity.getTransferencia().getId());

        transferencia.setSaldo(transferencia.getSaldo().subtract(entity.getValor()));
        if (transferencia.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            transferencia.setStatusPagamento(StatusPagamento.ESTORNADO);
            if (transferencia.getDataConciliacao() == null) {
                transferencia.setDataConciliacao(sistemaFacade.getDataOperacao());
            }
            if (transferencia.getRecebida() == null) {
                transferencia.setRecebida(sistemaFacade.getDataOperacao());
            }
            entity.setDataConciliacao(sistemaFacade.getDataOperacao());
            entity.setRecebida(sistemaFacade.getDataOperacao());
        }
        if (transferencia.getSubContaRetirada().getContaBancariaEntidade().equals(transferencia.getSubContaDeposito().getContaBancariaEntidade())) {
            entity.setDataConciliacao(sistemaFacade.getDataOperacao());
            entity.setRecebida(sistemaFacade.getDataOperacao());
        }
        em.merge(transferencia);
    }

    @Override
    public void salvar(EstornoTransferencia entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    public void salvarEstornoIntegracaoArquivoRetornoBancario(EstornoTransferencia estorno) {
        ConfiguracaoTranferenciaFinanceira recuperaEventoRetirada = configuracaoTranferenciaFinanceiraFacade.recuperaEvento(TipoLancamento.ESTORNO, estorno.getTransferencia().getTipoTransferenciaFinanceira(), OrigemTipoTransferencia.CONCEDIDO, estorno.getTransferencia().getResultanteIndependente(), estorno.getDataEstorno());
        ConfiguracaoTranferenciaFinanceira recuperaEventoRecebido = configuracaoTranferenciaFinanceiraFacade.recuperaEvento(TipoLancamento.ESTORNO, estorno.getTransferencia().getTipoTransferenciaFinanceira(), OrigemTipoTransferencia.RECEBIDO, estorno.getTransferencia().getResultanteIndependente(), estorno.getDataEstorno());
        estorno.setHistorico("Estorno referente inconsistência na integração bancária!");
        estorno.setEventoContabil(recuperaEventoRecebido.getEventoContabil());
        estorno.setEventoContabilRetirada(recuperaEventoRetirada.getEventoContabil());
        estorno.setDataConciliacao(new Date());
        salvarNovo(estorno);
    }

    public boolean existeEstornoComNumero(String numero) {
        String sql = "SELECT * FROM ESTORNOTRANSFERENCIA WHERE NUMERO = :numero";
        Query q = em.createNativeQuery(sql);
        q.setParameter("numero", numero);
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    public String getUltimoNumero() {
        String sql = "SELECT MAX(TO_NUMBER(CR.NUMERO))+1 AS ULTIMONUMERO FROM ESTORNOTRANSFERENCIA CR";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public void contabilizarTransferenciaDeposito(EstornoTransferencia entity) throws ExcecaoNegocioGenerica {
        ConfiguracaoTranferenciaFinanceira configuracaoTranferenciaFinanceira = configuracaoTranferenciaFinanceiraFacade.recuperaEvento(TipoLancamento.ESTORNO, entity.getTransferencia().getTipoTransferenciaFinanceira(), OrigemTipoTransferencia.RECEBIDO, entity.getTransferencia().getResultanteIndependente(), entity.getDataEstorno());
        if (configuracaoTranferenciaFinanceira != null && configuracaoTranferenciaFinanceira.getEventoContabil() != null) {
            entity.setEventoContabil(configuracaoTranferenciaFinanceira.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Estorno de Transferência (Depósito).");
        }

        if (entity.getEventoContabil() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataEstorno());
            parametroEvento.setUnidadeOrganizacional(entity.getTransferencia().getUnidadeOrganizacional());
            parametroEvento.setExercicio(entity.getExercicio());
            parametroEvento.setEventoContabil(entity.getEventoContabil());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.RECEBIDO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getId().toString(), EstornoTransferencia.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getTransferencia().getSubContaDeposito().getId().toString(), SubConta.class.getSimpleName(), item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);
            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada.");
        }
    }

    public void contabilizarTransferenciaRetirada(EstornoTransferencia entity) throws ExcecaoNegocioGenerica {
        ConfiguracaoTranferenciaFinanceira configuracaoTranferenciaFinanceira = configuracaoTranferenciaFinanceiraFacade.recuperaEvento(TipoLancamento.ESTORNO, entity.getTransferencia().getTipoTransferenciaFinanceira(), OrigemTipoTransferencia.CONCEDIDO, entity.getTransferencia().getResultanteIndependente(), entity.getDataEstorno());
        if (configuracaoTranferenciaFinanceira != null && configuracaoTranferenciaFinanceira.getEventoContabil() != null) {
            entity.setEventoContabilRetirada(configuracaoTranferenciaFinanceira.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Estorno de Transferência (Retirada).");
        }
        if (entity.getEventoContabilRetirada() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataEstorno());
            parametroEvento.setUnidadeOrganizacional(entity.getTransferencia().getUnidadeOrgConcedida());
            parametroEvento.setExercicio(entity.getExercicio());
            parametroEvento.setEventoContabil(entity.getEventoContabilRetirada());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.CONCEDIDO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);


            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getId().toString(), EstornoTransferencia.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getTransferencia().getSubContaRetirada().getId().toString(), SubConta.class.getSimpleName(), item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada.");
        }
    }

    public ConfiguracaoTranferenciaFinanceiraFacade getConfiguracaoTranferenciaFinanceiraFacade() {
        return configuracaoTranferenciaFinanceiraFacade;
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

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        ParametroEvento.ComplementoId complementoId = ((SuperEntidadeContabilFinanceira) entidadeContabil).getComplementoId();
        if (complementoId.equals(ParametroEvento.ComplementoId.NORMAL)
            || complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            contabilizarTransferenciaRetirada((EstornoTransferencia) entidadeContabil);
        }
        if (complementoId.equals(ParametroEvento.ComplementoId.NORMAL)
            || complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            contabilizarTransferenciaDeposito((EstornoTransferencia) entidadeContabil);
        }
    }

    public void estornarConciliacao(EstornoTransferencia estornoTransferencia) {
        try {
            estornoTransferencia.setDataConciliacao(null);
            estornoTransferencia.setRecebida(null);
            getEntityManager().merge(estornoTransferencia);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a conciliação. Consulte o suporte!");
        }
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    public List<EstornoTransferencia> buscarEstornoTransferencia(Exercicio ano, List<HierarquiaOrganizacional> listaUnidades) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }

        String hql = " select est from EstornoTransferencia est" +
            " where est.transferencia.unidadeOrganizacional in (:unidades)" +
            " and est.exercicio.id = :ano";
        Query consulta = em.createQuery(hql, EstornoTransferencia.class);
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
    public List<EstornoTransferencia> recuperarObjetosReprocessamento(EventosReprocessar er, Boolean isReprocessamentoInicial, ParametroEvento.ComplementoId complementoId) {

        String sql = "select est.* from EstornoTransferencia est " +
            " inner join TRANSFERENCIACONTAFINANC trans on est.transferencia_id = trans.id" +
            " where trunc(est.dataEstorno) between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')";
        if (er.hasUnidades() && complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            sql = sql + " and trans.unidadeOrgConcedida_id in (:unidades) ";
        }
        if (er.hasUnidades() && complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            sql = sql + " and trans.unidadeOrganizacional_id in (:unidades) ";
        }
        if (!isReprocessamentoInicial && complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            sql += " and trans.eventoContabilRetirada_id = :evento";
        }
        if (!isReprocessamentoInicial && complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            sql += " and trans.eventoContabil_id = :evento";
        }

        sql += " order by trunc(est.dataEstorno) asc";

        Query q = em.createNativeQuery(sql, EstornoTransferencia.class);
        try {
            q.setParameter("di", DataUtil.getDataFormatada(er.getDataInicial()));
            q.setParameter("df", DataUtil.getDataFormatada(er.getDataFinal()));
            if (er.hasUnidades()) {
                q.setParameter("unidades", er.getIdUnidades());
            }
            if (!isReprocessamentoInicial) {
                q.setParameter("evento", er.getEventoContabil());
            }
            List<EstornoTransferencia> retorno = q.getResultList();
            for (EstornoTransferencia entidade : retorno) {
                entidade.setComplementoId(complementoId);
            }
            return retorno;
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }


    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(EstornoTransferencia.class, filtros);
        consulta.incluirJoinsComplementar(" inner join TRANSFERENCIACONTAFINANC transferencia on obj.transferencia_id = transferencia.id ");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabilRetirada_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "transferencia.fonteDeRecursosDeposito_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "transferencia.fonteDeRecursosRetirada_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "transferencia.subContaDeposito_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "transferencia.subContaRetirada_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        return Arrays.asList(consulta);
    }
}
