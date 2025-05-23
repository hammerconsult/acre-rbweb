/*
 * Codigo gerado automaticamente em Mon Oct 01 11:27:54 BRT 2012
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
import br.com.webpublico.util.Util;
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
public class EstornoTransfMesmaUnidadeFacade extends SuperFacadeContabil<EstornoTransfMesmaUnidade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private TransferenciaMesmaUnidadeFacade transferenciaMesmaUnidadeFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private ConfiguracaoTranferenciaMesmaUnidadeFacade configuracaoTranferenciaMesmaUnidadeFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
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

    public EstornoTransfMesmaUnidadeFacade() {
        super(EstornoTransfMesmaUnidade.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public TransferenciaMesmaUnidadeFacade getTransferenciaMesmaUnidadeFacade() {
        return transferenciaMesmaUnidadeFacade;
    }

    public String getUltimoNumero() {
        String sql = "SELECT max(to_number(be.numero))+1 AS ultimoNumero FROM ESTORNOTRANSFMESMAUNIDADE be";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public boolean existeBensEstoqueComNumero(String numero) {
        String sql = "SELECT * FROM ESTORNOTRANSFMESMAUNIDADE WHERE NUMERO = :numero";
        Query q = em.createNativeQuery(sql);
        q.setParameter("numero", numero);
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public void salvar(EstornoTransfMesmaUnidade entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    @Override
    public void salvarNovo(EstornoTransfMesmaUnidade entity) {
        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getTransferenciaMesmaUnidade().getUnidadeOrganizacionalAdm(), entity.getTransferenciaMesmaUnidade().getUnidadeOrganizacional(), entity.getDataEstorno());
                validaSalvarEstorno(entity);
                movimentarTransferencia(entity);
                if (entity.getId() == null) {
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroTranferenciaMesmaUnidade(entity.getTransferenciaMesmaUnidade().getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataEstorno()));
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                geraSaldoContaFinanceiraConcedido(entity);
                gerarSaldoContaFinanceiraRecebido(entity);

                contabilizarTransferenciaDeposito(entity);
                contabilizarTransferenciaRetirada(entity);
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void geraSaldoContaFinanceiraConcedido(EstornoTransfMesmaUnidade entity) {
        saldoSubContaFacade.gerarSaldoFinanceiro(entity.getUnidadeOrganizacional(),
            entity.getTransferenciaMesmaUnidade().getSubContaRetirada(),
            entity.getTransferenciaMesmaUnidade().getContaDeDestinacaoRetirada(),
            entity.getValor(),
            TipoOperacao.CREDITO,
            entity.getDataEstorno(),
            entity.getEventoContabilRetirada(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.ESTORNO_DA_TRANFERENCIA_FINANCEIRA_DE_MESMA_UNIDADE,
            entity.getUuid(),
            true);
    }

    private void gerarSaldoContaFinanceiraRecebido(EstornoTransfMesmaUnidade entity) {
        saldoSubContaFacade.gerarSaldoFinanceiro(recuperaUnidadeDaSubContaRecebida(entity, sistemaFacade.getExercicioCorrente()),
            entity.getTransferenciaMesmaUnidade().getSubContaDeposito(),
            entity.getTransferenciaMesmaUnidade().getContaDeDestinacaoDeposito(),
            entity.getValor(),
            TipoOperacao.DEBITO,
            entity.getDataEstorno(),
            entity.getEventoContabil(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.ESTORNO_DA_TRANFERENCIA_FINANCEIRA_DE_MESMA_UNIDADE,
            entity.getUuid(),
            true);
    }

    private void movimentarTransferencia(EstornoTransfMesmaUnidade entity) {
        TransferenciaMesmaUnidade transferencia = transferenciaMesmaUnidadeFacade.recuperar(entity.getTransferenciaMesmaUnidade().getId());
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

    public void validaSalvarEstorno(EstornoTransfMesmaUnidade estorno) {
        if (estorno.getValor() == null || estorno.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ExcecaoNegocioGenerica(" O campo valor deve ser maior que 0(zero).");
        }
        if (estorno.getValor().compareTo(estorno.getTransferenciaMesmaUnidade().getSaldo()) > 0) {
            throw new ExcecaoNegocioGenerica("O Valor a ser estornado de " + "<b>" + Util.formataValor(estorno.getValor()) + "</b>" + " não pode ser maior que o saldo de " + "<b>" + Util.formataValor(estorno.getTransferenciaMesmaUnidade().getSaldo()) + "</b>" + " disponível na transferência.");
        }

        if (DataUtil.dataSemHorario(estorno.getDataEstorno()).before(DataUtil.dataSemHorario(estorno.getTransferenciaMesmaUnidade().getDataTransferencia()))) {
            throw new ExcecaoNegocioGenerica(" A data do estorno deve ser maior ou igual a data da transferência. Data da Transferência: <b>" + DataUtil.getDataFormatada(estorno.getTransferenciaMesmaUnidade().getDataTransferencia()) + "</b>.");
        }
        if (estorno.getEventoContabil() == null) {
            throw new ExcecaoNegocioGenerica(" Não foi encontrado o evento contábil de recebido para este lançamento.");
        }
        if (estorno.getEventoContabilRetirada() == null) {
            throw new ExcecaoNegocioGenerica(" Não foi encontrado o evento contábil de concedido para este lançemento.");
        }

    }

    public void salvarEstornoTranferenciaMesmaUnidadeArquivoRetornoBancario(EstornoTransfMesmaUnidade estorno) {
        try {
            ConfiguracaoTranferenciaMesmaUnidade recuperaEventoCon;
            recuperaEventoCon = getConfiguracaoTranferenciaMesmaUnidadeFacade().recuperaEvento(TipoLancamento.ESTORNO, estorno.getTransferenciaMesmaUnidade().getResultanteIndependente(), estorno.getTransferenciaMesmaUnidade().getTipoTransferencia(), OrigemTipoTransferencia.CONCEDIDO, estorno.getDataEstorno());
            ConfiguracaoTranferenciaMesmaUnidade recuperaEventoRec;
            recuperaEventoRec = getConfiguracaoTranferenciaMesmaUnidadeFacade().recuperaEvento(TipoLancamento.ESTORNO, estorno.getTransferenciaMesmaUnidade().getResultanteIndependente(), estorno.getTransferenciaMesmaUnidade().getTipoTransferencia(), OrigemTipoTransferencia.RECEBIDO, estorno.getDataEstorno());
            estorno.setHistorico("Estorno referente inconsistência na integração bancária!");
            estorno.setEventoContabil(recuperaEventoRec.getEventoContabil());
            estorno.setEventoContabilRetirada(recuperaEventoCon.getEventoContabil());
            salvarNovo(estorno);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao salvar o estorno de tranferência integrada ao arquivo de retorno do banco. " + ex.getMessage());
        }
    }

    public List<EstornoTransfMesmaUnidade> listaEstornoTransfMesmaUnidadePorUnidade(UnidadeOrganizacional uo) {
        String sql = "SELECT * FROM ESTORNOTRANSFMESMAUNIDADE WHERE UNIDADEORGANIZACIONAL_ID = :uo ORDER BY id DESC";
        Query q = em.createNativeQuery(sql, EstornoTransfMesmaUnidade.class);
        q.setParameter("uo", uo.getId());
        return q.getResultList();
    }

    public ConfiguracaoTranferenciaMesmaUnidadeFacade getConfiguracaoTranferenciaMesmaUnidadeFacade() {
        return configuracaoTranferenciaMesmaUnidadeFacade;
    }

    public void contabilizarTransferenciaDeposito(EstornoTransfMesmaUnidade entity) throws ExcecaoNegocioGenerica {

        ConfiguracaoTranferenciaMesmaUnidade configuracaoTranferenciaMesmaUnidade = transferenciaMesmaUnidadeFacade.getConfiguracaoTranferenciaMesmaUnidadeFacade().recuperaEvento(TipoLancamento.ESTORNO, entity.getTransferenciaMesmaUnidade().getResultanteIndependente(), entity.getTransferenciaMesmaUnidade().getTipoTransferencia(), OrigemTipoTransferencia.RECEBIDO, entity.getDataEstorno());
        if (configuracaoTranferenciaMesmaUnidade != null && configuracaoTranferenciaMesmaUnidade.getEventoContabil() != null) {
            entity.setEventoContabil(configuracaoTranferenciaMesmaUnidade.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Estorno de Transferência de Mesma Unidade (Deposito).");
        }
        if (entity.getEventoContabil() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataEstorno());
            parametroEvento.setUnidadeOrganizacional(entity.getTransferenciaMesmaUnidade().getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(entity.getEventoContabil());
            parametroEvento.setExercicio(entity.getTransferenciaMesmaUnidade().getExercicio());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.RECEBIDO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);


            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getId().toString(), EstornoTransfMesmaUnidade.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getTransferenciaMesmaUnidade().getSubContaDeposito().getId().toString(), SubConta.class.getSimpleName(), item));
            item.setObjetoParametros(objetos);


            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada.");
        }
    }

    public void contabilizarTransferenciaRetirada(EstornoTransfMesmaUnidade entity) throws ExcecaoNegocioGenerica {
        ConfiguracaoTranferenciaMesmaUnidade configuracaoTranferenciaMesmaUnidade = transferenciaMesmaUnidadeFacade.getConfiguracaoTranferenciaMesmaUnidadeFacade().recuperaEvento(TipoLancamento.ESTORNO, entity.getTransferenciaMesmaUnidade().getResultanteIndependente(), entity.getTransferenciaMesmaUnidade().getTipoTransferencia(), OrigemTipoTransferencia.CONCEDIDO, entity.getDataEstorno());
        if (configuracaoTranferenciaMesmaUnidade != null && configuracaoTranferenciaMesmaUnidade.getEventoContabil() != null) {
            entity.setEventoContabilRetirada(configuracaoTranferenciaMesmaUnidade.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Estorno de Transferência de Mesma Unidade (Retirada).");
        }
        if (entity.getEventoContabilRetirada() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataEstorno());
            parametroEvento.setUnidadeOrganizacional(entity.getTransferenciaMesmaUnidade().getUnidadeOrganizacional());
            parametroEvento.setExercicio(entity.getTransferenciaMesmaUnidade().getExercicio());
            parametroEvento.setEventoContabil(entity.getEventoContabilRetirada());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.CONCEDIDO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);


            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getId().toString(), EstornoTransfMesmaUnidade.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getTransferenciaMesmaUnidade().getSubContaRetirada().getId().toString(), SubConta.class.getSimpleName(), item));
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

    public SingletonGeradorCodigoContabil getSingletonGeradorCodigoContabil() {
        return singletonGeradorCodigoContabil;
    }

    public void setSingletonGeradorCodigoContabil(SingletonGeradorCodigoContabil singletonGeradorCodigoContabil) {
        this.singletonGeradorCodigoContabil = singletonGeradorCodigoContabil;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public void setReprocessamentoLancamentoContabilSingleton(ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton) {
        this.reprocessamentoLancamentoContabilSingleton = reprocessamentoLancamentoContabilSingleton;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarTransferenciaDeposito((EstornoTransfMesmaUnidade) entidadeContabil);
        contabilizarTransferenciaRetirada((EstornoTransfMesmaUnidade) entidadeContabil);
    }

    public void estornarConciliacao(EstornoTransfMesmaUnidade estornoTransfMesmaUnidade) {
        try {
            estornoTransfMesmaUnidade.setDataConciliacao(null);
            estornoTransfMesmaUnidade.setRecebida(null);
            getEntityManager().merge(estornoTransfMesmaUnidade);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a conciliação. Consulte o suporte!");
        }
    }

    public UnidadeOrganizacional recuperaUnidadeDaSubContaRecebida(EstornoTransfMesmaUnidade selecionado, Exercicio exercicio) {
        try {
            if (selecionado.getTransferenciaMesmaUnidade().getFonteDeRecursosDeposito() != null) {
                selecionado.getTransferenciaMesmaUnidade().getSubContaRetirada().setContaVinculada(transferenciaMesmaUnidadeFacade.getSubContaFacade().recuperar(selecionado.getTransferenciaMesmaUnidade().getSubContaRetirada().getContaVinculada().getId()));
                for (SubContaUniOrg subContaUniOrg : selecionado.getTransferenciaMesmaUnidade().getSubContaRetirada().getContaVinculada().getUnidadesOrganizacionais()) {
                    if (subContaUniOrg.getExercicio().equals(exercicio)) {
                        return subContaUniOrg.getUnidadeOrganizacional();
                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(EstornoTransfMesmaUnidade.class, filtros);
        consulta.incluirJoinsComplementar(" inner join transferenciaMesmaUnidade transferencia on obj.transferenciaMesmaUnidade_id = transferencia.id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "obj.dataEstorno");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "obj.dataEstorno");
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
