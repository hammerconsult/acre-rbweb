/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.TransferenciaContaFinanceiraPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.financeiro.SuperEntidadeContabilFinanceira;
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
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Claudio
 */
@Stateless
public class TransferenciaContaFinanceiraFacade extends SuperFacadeContabil<TransferenciaContaFinanceira> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ConfiguracaoTranferenciaFinanceiraFacade configuracaoTranferenciaFinanceiraFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private FinalidadePagamentoFacade finalidadePagamentoFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;

    public TransferenciaContaFinanceiraFacade() {
        super(TransferenciaContaFinanceira.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    @Override
    public void salvar(TransferenciaContaFinanceira entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void salvarNovo(TransferenciaContaFinanceira entity) {
        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                movimentarTransferencia(entity);
                if (entity.getId() == null) {
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroTranferenciaContaFinanceira(entity.getExercicio(), entity.getUnidadeOrgConcedida(), entity.getDataTransferencia()));
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                gerarSaldoFinanceiro(entity);

                contabilizarTransferenciaDeposito(entity);
                contabilizarTransferenciaRetirada(entity);

                portalTransparenciaNovoFacade.salvarPortal(new TransferenciaContaFinanceiraPortal(entity));
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void gerarSaldoFinanceiro(TransferenciaContaFinanceira entity) {
        geraSaldoContaFinanceiraConcedida(entity);
        geraSaldoContaFinanceiraRecebida(entity);
    }

    private void geraSaldoContaFinanceiraConcedida(TransferenciaContaFinanceira entity) {
        saldoSubContaFacade.gerarSaldoFinanceiro(
            entity.getUnidadeOrgConcedida(),
            entity.getSubContaRetirada(),
            entity.getContaDeDestinacaoRetirada(),
            entity.getValor(),
            TipoOperacao.DEBITO,
            entity.getDataTransferencia(),
            entity.getEventoContabilRetirada(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.TRANSFERENCIA_FINANCEIRA,
            entity.getUuid(),
            true);
    }

    private void geraSaldoContaFinanceiraRecebida(TransferenciaContaFinanceira entity) {
        saldoSubContaFacade.gerarSaldoFinanceiro(entity.getUnidadeOrganizacional(),
            entity.getSubContaDeposito(),
            entity.getContaDeDestinacaoDeposito(),
            entity.getValor(),
            TipoOperacao.CREDITO,
            entity.getDataTransferencia(),
            entity.getEventoContabil(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.TRANSFERENCIA_FINANCEIRA,
            entity.getUuid(),
            true);
    }

    private void movimentarTransferencia(TransferenciaContaFinanceira entity) {
        hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataTransferencia());
        if (entity.getSubContaDeposito().getContaBancariaEntidade().getId().equals(entity.getSubContaRetirada().getContaBancariaEntidade().getId())) {
            entity.setStatusPagamento(StatusPagamento.PAGO);
            entity.setSaldo(BigDecimal.ZERO);
        } else {
            entity.setStatusPagamento(StatusPagamento.DEFERIDO);
            entity.setSaldo(entity.getValor());
        }
    }

    public List<TransferenciaContaFinanceira> listaFiltrandoPorBancoUnidade(Banco banco, UnidadeOrganizacional unidadeOrganizacional, SubConta subConta, Exercicio exercicio) {
        String sql = "SELECT TCF.* "
            + " FROM TRANSFERENCIACONTAFINANC TCF"
            + " INNER JOIN SUBCONTA SC ON TCF.SUBCONTARETIRADA_ID = SC.ID"
            + " INNER JOIN CONTABANCARIAENTIDADE CBE ON SC.CONTABANCARIAENTIDADE_ID = CBE.ID"
            + " INNER JOIN AGENCIA AG ON CBE.AGENCIA_ID = AG.ID"
            + " INNER JOIN BANCO BC ON AG.BANCO_ID = BC.ID"
            + " WHERE TCF.UNIDADEORGCONCEDIDA_ID = :uo"
            + " AND TCF.TIPOOPERACAOPAGTO <> '" + TipoOperacaoPagto.BAIXA_PARA_REGULARIZACAO + "' "
            + " AND (TCF.STATUSPAGAMENTO = '" + StatusPagamento.DEFERIDO.name() + "' or  TCF.STATUSPAGAMENTO = '" + StatusPagamento.INDEFERIDO.name() + "')"
            + "  AND BC.ID = :banco"
            + "  AND SC.ID = :idSubconta"
            + "  AND TCF.EXERCICIO_ID = :idExercicio";
        Query q = em.createNativeQuery(sql, TransferenciaContaFinanceira.class);
        q.setParameter("banco", banco.getId());
        q.setParameter("idSubconta", subConta.getId());
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("uo", unidadeOrganizacional.getId());
        return q.getResultList();
    }

    public List<TransferenciaContaFinanceira> listaFiltrandoPorUnidadeESaldo(String parte, UnidadeOrganizacional unidadeOrganizacional, Exercicio exercicio) {
        String sql = " SELECT TRANSF.* "
            + " FROM TRANSFERENCIACONTAFINANC TRANSF "
            + " WHERE TRANSF.UNIDADEORGCONCEDIDA_ID = :uo "
            + " AND ((LOWER(TRANSF.NUMERO) LIKE :parte) "
            + "      OR (LOWER(TRANSF.VALOR) LIKE :parte)) "
            + " AND ROWNUM <= 10 "
            + " AND TRANSF.SALDO > 0 "
            + " AND TRANSF.EXERCICIO_ID = :idExercicio "
            + " AND (TRANSF.STATUSPAGAMENTO = + '" + StatusPagamento.DEFERIDO.name() + "' "
            + "       or TRANSF.STATUSPAGAMENTO = '" + StatusPagamento.INDEFERIDO.name() + "')"
            + " ORDER BY TRANSF.NUMERO DESC ";
        Query q = em.createNativeQuery(sql, TransferenciaContaFinanceira.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("uo", unidadeOrganizacional.getId());
        q.setParameter("idExercicio", exercicio.getId());
        return q.getResultList();
    }

    public String getUltimoNumero() {
        String sql = "SELECT MAX(TO_NUMBER(CR.NUMERO))+1 AS ULTIMONUMERO FROM TRANSFERENCIACONTAFINANC CR";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public void contabilizarTransferenciaDeposito(TransferenciaContaFinanceira entity) throws ExcecaoNegocioGenerica {
        ConfiguracaoTranferenciaFinanceira configuracaoTranferenciaFinanceira = configuracaoTranferenciaFinanceiraFacade.recuperaEvento(TipoLancamento.NORMAL, entity.getTipoTransferenciaFinanceira(), OrigemTipoTransferencia.RECEBIDO, entity.getResultanteIndependente(), entity.getDataTransferencia());
        if (configuracaoTranferenciaFinanceira != null && configuracaoTranferenciaFinanceira.getEventoContabil() != null) {
            entity.setEventoContabil(configuracaoTranferenciaFinanceira.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Transferência (Depósito).");
        }

        if (entity.getEventoContabil() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = criarParametroEventoRecebido(entity);
            contabilizacaoFacade.contabilizar(parametroEvento);
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada.");
        }
    }

    public ParametroEvento criarParametroEventoRecebido(TransferenciaContaFinanceira entity) {
        try {
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataTransferencia());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            parametroEvento.setExercicio(entity.getExercicio());
            parametroEvento.setEventoContabil(entity.getEventoContabil());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId() == null ? null : entity.getId().toString());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.RECEBIDO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getId().toString(), TransferenciaContaFinanceira.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getSubContaDeposito().getId().toString(), SubConta.class.getSimpleName(), item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    public void contabilizarTransferenciaRetirada(TransferenciaContaFinanceira entity) throws ExcecaoNegocioGenerica {
        ConfiguracaoTranferenciaFinanceira configuracaoTranferenciaFinanceira = configuracaoTranferenciaFinanceiraFacade.recuperaEvento(TipoLancamento.NORMAL, entity.getTipoTransferenciaFinanceira(), OrigemTipoTransferencia.CONCEDIDO, entity.getResultanteIndependente(), entity.getDataTransferencia());
        if (configuracaoTranferenciaFinanceira != null && configuracaoTranferenciaFinanceira.getEventoContabil() != null) {
            entity.setEventoContabilRetirada(configuracaoTranferenciaFinanceira.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Transferência (Retirada).");
        }

        if (entity.getEventoContabilRetirada() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = criarParametroEventoConcedido(entity);
            contabilizacaoFacade.contabilizar(parametroEvento);
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada.");
        }
    }

    public ParametroEvento criarParametroEventoConcedido(TransferenciaContaFinanceira entity) {
        try {
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataTransferencia());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrgConcedida());
            parametroEvento.setExercicio(entity.getExercicio());
            parametroEvento.setEventoContabil(entity.getEventoContabilRetirada());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId() == null ? null : entity.getId().toString());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.CONCEDIDO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);


            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getId().toString(), TransferenciaContaFinanceira.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getSubContaRetirada().getId().toString(), SubConta.class.getSimpleName(), item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ConfiguracaoTranferenciaFinanceiraFacade getConfiguracaoTranferenciaFinanceiraFacade() {
        return configuracaoTranferenciaFinanceiraFacade;
    }

    @Override
    public TransferenciaContaFinanceira recuperar(Object id) {
        TransferenciaContaFinanceira transferenciaContaFinanceira = em.find(TransferenciaContaFinanceira.class, id);
        transferenciaContaFinanceira.getSubContaDeposito().getUnidadesOrganizacionais().size();
        transferenciaContaFinanceira.getSubContaRetirada().getUnidadesOrganizacionais().size();
        transferenciaContaFinanceira.getSubContaDeposito().getSubContaFonteRecs().size();
        transferenciaContaFinanceira.getSubContaRetirada().getSubContaFonteRecs().size();
        return transferenciaContaFinanceira;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public FinalidadePagamentoFacade getFinalidadePagamentoFacade() {
        return finalidadePagamentoFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        ParametroEvento.ComplementoId complementoId = ((SuperEntidadeContabilFinanceira) entidadeContabil).getComplementoId();
        if (complementoId.equals(ParametroEvento.ComplementoId.NORMAL)
            || complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            contabilizarTransferenciaRetirada((TransferenciaContaFinanceira) entidadeContabil);
        }
        if (complementoId.equals(ParametroEvento.ComplementoId.NORMAL)
            || complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            contabilizarTransferenciaDeposito((TransferenciaContaFinanceira) entidadeContabil);
        }
    }

    public void estornarConciliacao(TransferenciaContaFinanceira transferenciaContaFinanceira) {
        try {
            transferenciaContaFinanceira.setDataConciliacao(null);
            transferenciaContaFinanceira.setRecebida(null);
            getEntityManager().merge(transferenciaContaFinanceira);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a conciliação. Consulte o suporte!");
        }
    }

    public void baixar(TransferenciaContaFinanceira transferenciaContaFinanceira) throws ExcecaoNegocioGenerica {
        try {
            transferenciaContaFinanceira.setSaldo(transferenciaContaFinanceira.getSaldo().subtract(transferenciaContaFinanceira.getSaldo()));
            transferenciaContaFinanceira.setStatusPagamento(StatusPagamento.PAGO);
            getEntityManager().merge(transferenciaContaFinanceira);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao baixar. Consulte o suporte!");
        }
    }

    public void estornarBaixa(TransferenciaContaFinanceira transferenciaContaFinanceira) {
        try {
            if (listaEstornoTransfFinanceira(transferenciaContaFinanceira).isEmpty()) {
                transferenciaContaFinanceira.setSaldo(transferenciaContaFinanceira.getSaldo().add(transferenciaContaFinanceira.getValor()));
                transferenciaContaFinanceira.setStatusPagamento(StatusPagamento.DEFERIDO);
            } else {
                BigDecimal valor;
                valor = transferenciaContaFinanceira.getValor().subtract(getSomaEstornosTransfFinanceira(transferenciaContaFinanceira));
                transferenciaContaFinanceira.setSaldo(transferenciaContaFinanceira.getSaldo().add(valor));
                transferenciaContaFinanceira.setStatusPagamento(StatusPagamento.DEFERIDO);
            }
            getEntityManager().merge(transferenciaContaFinanceira);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a baixa. Consulte o suporte! " + e.getMessage());
        }
    }

    public BigDecimal getSomaEstornosTransfFinanceira(TransferenciaContaFinanceira transferenciaContaFinanceira) {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (EstornoTransferencia est : listaEstornoTransfFinanceira(transferenciaContaFinanceira)) {
            estornos = estornos.add(est.getValor());
        }
        return estornos;
    }

    public List<EstornoTransferencia> listaEstornoTransfFinanceira(TransferenciaContaFinanceira transf) {
        String sql = " select est.* from estornotransferencia est " +
            " inner join transferenciacontafinanc transf on transf.id = est.transferencia_id " +
            " where transf.id = :idTransf ";
        Query q = em.createNativeQuery(sql, EstornoTransferencia.class);
        q.setParameter("idTransf", transf.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    public List<TransferenciaContaFinanceira> buscarTransferencia(Exercicio ano, List<HierarquiaOrganizacional> listaUnidades) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }

        String hql = " select trans from TransferenciaContaFinanceira trans " +
            " where trans.unidadeOrganizacional in (:unidades)" +
            " and trans.exercicio.id = :ano";
        Query consulta = em.createQuery(hql, TransferenciaContaFinanceira.class);
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
    public List<TransferenciaContaFinanceira> recuperarObjetosReprocessamento(EventosReprocessar er, Boolean isReprocessamentoInicial, ParametroEvento.ComplementoId complementoId) {

        String sql = "select trans.* from TRANSFERENCIACONTAFINANC trans" +
            " where trunc(trans.DATATRANSFERENCIA) between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')";
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

        sql += " order by trans.DATATRANSFERENCIA asc";

        Query q = em.createNativeQuery(sql, TransferenciaContaFinanceira.class);
        try {
            q.setParameter("di", DataUtil.getDataFormatada(er.getDataInicial()));
            q.setParameter("df", DataUtil.getDataFormatada(er.getDataFinal()));
            if (er.hasUnidades()) {
                q.setParameter("unidades", er.getIdUnidades());
            }
            if (!isReprocessamentoInicial) {
                q.setParameter("evento", er.getEventoContabil());
            }
            List<TransferenciaContaFinanceira> retorno = q.getResultList();
            for (TransferenciaContaFinanceira entidade : retorno) {
                entidade.setComplementoId(complementoId);
            }
            return retorno;
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(TransferenciaContaFinanceira.class, filtros);
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataTransferencia)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataTransferencia)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabilRetirada_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "obj.fonteDeRecursosDeposito_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "obj.fonteDeRecursosRetirada_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "obj.subContaDeposito_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "obj.subContaRetirada_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicorazao");
        return Arrays.asList(consulta);
    }
}
