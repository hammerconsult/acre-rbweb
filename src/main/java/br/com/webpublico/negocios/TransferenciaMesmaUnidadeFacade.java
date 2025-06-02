/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Claudio
 */
@Stateless
public class TransferenciaMesmaUnidadeFacade extends SuperFacadeContabil<TransferenciaMesmaUnidade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ConfiguracaoTranferenciaMesmaUnidadeFacade configuracaoTranferenciaMesmaUnidadeFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
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

    public TransferenciaMesmaUnidadeFacade() {
        super(TransferenciaMesmaUnidade.class);
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

    public ConfiguracaoTranferenciaMesmaUnidadeFacade getConfiguracaoTranferenciaMesmaUnidadeFacade() {
        return configuracaoTranferenciaMesmaUnidadeFacade;
    }

    public void baixarTransferenciaAoSalvar(TransferenciaMesmaUnidade entity) {
        if (entity.getDataConciliacao() != null
            && entity.getRecebida() != null) {
            entity.setSaldo(BigDecimal.ZERO);
            entity.setStatusPagamento(StatusPagamento.PAGO);
        }
    }

    @Override
    public void salvar(TransferenciaMesmaUnidade entity) {
        entity.gerarHistoricos();
        baixarTransferenciaAoSalvar(entity);
        em.merge(entity);
    }

    @Override
    public void salvarNovo(TransferenciaMesmaUnidade entity) {
        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                movimentarTransferencia(entity);
                baixarTransferenciaAoSalvar(entity);
                if (entity.getId() == null) {
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroTranferenciaMesmaUnidade(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataTransferencia()));
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                gerarSaldoContaFinanceira(entity);
                contabilizarTransferenciaDeposito(entity);
                contabilizarTransferenciaRetirada(entity);
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void gerarSaldoContaFinanceira(TransferenciaMesmaUnidade entity) {

        saldoSubContaFacade.gerarSaldoFinanceiro(entity.getUnidadeOrganizacional(),
            entity.getSubContaRetirada(),
            entity.getContaDeDestinacaoRetirada(),
            entity.getValor(),
            TipoOperacao.DEBITO,
            entity.getDataTransferencia(),
            entity.getEventoContabilRetirada(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE,
            entity.getUuid(),
            true);

        saldoSubContaFacade.gerarSaldoFinanceiro(recuperaUnidadeDaSubContaConcedida(entity, entity.getExercicio()),
            entity.getSubContaDeposito(),
            entity.getContaDeDestinacaoDeposito(),
            entity.getValor(),
            TipoOperacao.CREDITO,
            entity.getDataTransferencia(),
            entity.getEventoContabil(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE,
            entity.getUuid(),
            true);
    }


    private void movimentarTransferencia(TransferenciaMesmaUnidade entity) {
        hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataTransferencia());
        if (entity.getSubContaDeposito().getContaBancariaEntidade().equals(entity.getSubContaRetirada().getContaBancariaEntidade())) {
            entity.setStatusPagamento(StatusPagamento.PAGO);
            entity.setSaldo(BigDecimal.ZERO);
        } else {
            entity.setSaldo(entity.getValor());
            entity.setStatusPagamento(StatusPagamento.DEFERIDO);
        }
    }

    public List<TransferenciaMesmaUnidade> listaFiltrandoPorBancoUnidade(Banco banco, UnidadeOrganizacional unidadeOrganizacional, SubConta subConta, Exercicio exercicio) {
        String sql = "SELECT TCF.* "
            + " FROM TRANSFERENCIAMESMAUNIDADE TCF"
            + " INNER JOIN SUBCONTA SC ON TCF.SUBCONTARETIRADA_ID = SC.ID"
            + " INNER JOIN CONTABANCARIAENTIDADE CBE ON SC.CONTABANCARIAENTIDADE_ID = CBE.ID"
            + " INNER JOIN AGENCIA AG ON CBE.AGENCIA_ID = AG.ID"
            + " INNER JOIN BANCO BC ON AG.BANCO_ID = BC.ID"
            + " WHERE TCF.UNIDADEORGANIZACIONAL_ID = :uo "
            + " AND TCF.TIPOOPERACAOPAGTO <> '" + TipoOperacaoPagto.BAIXA_PARA_REGULARIZACAO + "' "
            + " AND (TCF.STATUSPAGAMENTO = '" + StatusPagamento.DEFERIDO.name() + "' or  TCF.STATUSPAGAMENTO = '" + StatusPagamento.INDEFERIDO.name() + "')"
            + "  AND BC.ID = :banco"
            + "  AND TCF.EXERCICIO_ID = :idExercicio"
            + "  AND SC.ID = :idSubconta";
        Query q = em.createNativeQuery(sql, TransferenciaMesmaUnidade.class);
        q.setParameter("banco", banco.getId());
        q.setParameter("idSubconta", subConta.getId());
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("uo", unidadeOrganizacional.getId());

        return q.getResultList();
    }

    public String getUltimoNumero() {
        String sql = "SELECT MAX(TO_NUMBER(CR.NUMERO))+1 AS ULTIMONUMERO FROM TRANSFERENCIAMESMAUNIDADE CR";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public List<TransferenciaMesmaUnidade> listaFiltrandoTransferenciaPorUnidadeLogadaESaldoDisponivel(String parte, UnidadeOrganizacional uo, Exercicio ex) {
        String sql = "  SELECT T.* "
            + " FROM TRANSFERENCIAMESMAUNIDADE T "
            + " INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = T.UNIDADEORGANIZACIONAL_ID "
            + "  WHERE (LOWER (T.NUMERO) LIKE :parte) "
            + "      AND T.UNIDADEORGANIZACIONAL_ID = :uo "
            + "      AND T.EXERCICIO_ID = :ex "
            + "      AND T.SALDO > 0 "
            + "      AND (T.STATUSPAGAMENTO = '" + StatusPagamento.DEFERIDO.name() + "' "
            + "        or T.STATUSPAGAMENTO = '" + StatusPagamento.INDEFERIDO.name() + "')"
            + " ORDER BY T.DATATRANSFERENCIA, T.NUMERO DESC";
        Query q = em.createNativeQuery(sql, TransferenciaMesmaUnidade.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("uo", uo.getId());
        q.setParameter("ex", ex.getId());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public void contabilizarTransferenciaDeposito(TransferenciaMesmaUnidade entity) throws ExcecaoNegocioGenerica {
        ConfiguracaoTranferenciaMesmaUnidade configuracaoTranferenciaMesmaUnidade = configuracaoTranferenciaMesmaUnidadeFacade.recuperaEvento(TipoLancamento.NORMAL, entity.getResultanteIndependente(), entity.getTipoTransferencia(), OrigemTipoTransferencia.RECEBIDO, entity.getDataTransferencia());

        if (configuracaoTranferenciaMesmaUnidade != null && configuracaoTranferenciaMesmaUnidade.getEventoContabil() != null) {
            entity.setEventoContabil(configuracaoTranferenciaMesmaUnidade.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Transferência de Mesma Unidade (Recebida).");
        }

        if (entity.getEventoContabil() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataTransferencia());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(entity.getEventoContabil());
            parametroEvento.setExercicio(entity.getExercicio());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.RECEBIDO);
            ItemParametroEvento item = new ItemParametroEvento();

            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity, item));
            objetos.add(new ObjetoParametro(entity.getSubContaDeposito(), item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada.");
        }
    }

    public void contabilizarTransferenciaRetirada(TransferenciaMesmaUnidade entity) throws ExcecaoNegocioGenerica {
        ConfiguracaoTranferenciaMesmaUnidade configuracaoTranferenciaMesmaUnidade = configuracaoTranferenciaMesmaUnidadeFacade.recuperaEvento(TipoLancamento.NORMAL, entity.getResultanteIndependente(), entity.getTipoTransferencia(), OrigemTipoTransferencia.CONCEDIDO, entity.getDataTransferencia());
        if (configuracaoTranferenciaMesmaUnidade != null && configuracaoTranferenciaMesmaUnidade.getEventoContabil() != null) {
            entity.setEventoContabilRetirada(configuracaoTranferenciaMesmaUnidade.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Transferência de Mesma Unidade (Concedida).");
        }

        if (entity.getEventoContabilRetirada() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataTransferencia());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(entity.getEventoContabilRetirada());
            parametroEvento.setExercicio(entity.getExercicio());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.CONCEDIDO);
            ItemParametroEvento item = new ItemParametroEvento();

            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity, item));
            objetos.add(new ObjetoParametro(entity.getSubContaRetirada(), item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada.");
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
        contabilizarTransferenciaDeposito((TransferenciaMesmaUnidade) entidadeContabil);
        contabilizarTransferenciaRetirada((TransferenciaMesmaUnidade) entidadeContabil);
    }

    public void baixarTransferncia(TransferenciaMesmaUnidade selecionado) throws ExcecaoNegocioGenerica {
        try {
            selecionado.setStatusPagamento(StatusPagamento.PAGO);
            selecionado.setSaldo(BigDecimal.ZERO);
            getEntityManager().merge(selecionado);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao baixar a transferência. Consulte o suporte!");
        }
    }

    public UnidadeOrganizacional recuperaUnidadeDaSubContaConcedida(TransferenciaMesmaUnidade selecionado, Exercicio exercicio) {
        try {
            if (selecionado.getFonteDeRecursosDeposito() != null) {
                selecionado.getSubContaRetirada().setContaVinculada(getSubContaFacade().recuperar(selecionado.getSubContaRetirada().getContaVinculada().getId()));
                for (SubContaUniOrg subContaUniOrg : selecionado.getSubContaRetirada().getContaVinculada().getUnidadesOrganizacionais()) {
                    if (subContaUniOrg.getExercicio().equals(exercicio)) {
                        return subContaUniOrg.getUnidadeOrganizacional();
                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    public void estornarConciliacao(TransferenciaMesmaUnidade transferenciaMesmaUnidade) {
        try {
            transferenciaMesmaUnidade.setDataConciliacao(null);
            transferenciaMesmaUnidade.setRecebida(null);
            getEntityManager().merge(transferenciaMesmaUnidade);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a conciliação. Consulte o suporte!");
        }
    }

    public void baixar(TransferenciaMesmaUnidade transferenciaMesmaUnidade) throws ExcecaoNegocioGenerica {
        try {
            transferenciaMesmaUnidade.setSaldo(transferenciaMesmaUnidade.getSaldo().subtract(transferenciaMesmaUnidade.getSaldo()));
            transferenciaMesmaUnidade.setStatusPagamento(StatusPagamento.PAGO);
            getEntityManager().merge(transferenciaMesmaUnidade);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao baixar. Consulte o suporte!");
        }
    }

    public void estornarBaixa(TransferenciaMesmaUnidade transferenciaMesmaUnidade) {
        try {
            if (listaEstornoTransfMesmaUnidade(transferenciaMesmaUnidade).isEmpty()) {
                transferenciaMesmaUnidade.setSaldo(transferenciaMesmaUnidade.getSaldo().add(transferenciaMesmaUnidade.getValor()));
                transferenciaMesmaUnidade.setStatusPagamento(StatusPagamento.DEFERIDO);
            } else {
                BigDecimal valor;
                valor = transferenciaMesmaUnidade.getValor().subtract(getSomaEstornosTransfMesmaUnidade(transferenciaMesmaUnidade));
                transferenciaMesmaUnidade.setSaldo(transferenciaMesmaUnidade.getSaldo().add(valor));
                transferenciaMesmaUnidade.setStatusPagamento(StatusPagamento.DEFERIDO);
            }
            getEntityManager().merge(transferenciaMesmaUnidade);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a baixa. Consulte o suporte! " + e.getMessage());
        }
    }

    public BigDecimal getSomaEstornosTransfMesmaUnidade(TransferenciaMesmaUnidade transferenciaMesmaUnidade) {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (EstornoTransfMesmaUnidade est : listaEstornoTransfMesmaUnidade(transferenciaMesmaUnidade)) {
            estornos = estornos.add(est.getValor());
        }
        return estornos;
    }

    public List<EstornoTransfMesmaUnidade> listaEstornoTransfMesmaUnidade(TransferenciaMesmaUnidade transf) {
        String sql = " select est.* from estornotransfmesmaunidade est " +
            " inner join transferenciamesmaunidade transf on transf.id = est.transferenciamesmaunidade_id " +
            " where transf.id = :idTransf ";
        Query q = em.createNativeQuery(sql, EstornoTransfMesmaUnidade.class);
        q.setParameter("idTransf", transf.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(TransferenciaMesmaUnidade.class, filtros);
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
