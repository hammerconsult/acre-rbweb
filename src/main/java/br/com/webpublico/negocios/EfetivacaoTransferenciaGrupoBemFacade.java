package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.enums.TipoPrazo;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class EfetivacaoTransferenciaGrupoBemFacade extends AbstractFacade<EfetivacaoTransferenciaGrupoBem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private PesquisaBemFacade pesquisaBemFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private SolicitacaoTransferenciaGrupoBemFacade solicitacaoTransferenciaGrupoBemFacade;
    @EJB
    private IntegradorPatrimonialContabilFacade integradorPatrimonialContabilFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;

    public EfetivacaoTransferenciaGrupoBemFacade() {
        super(EfetivacaoTransferenciaGrupoBem.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EfetivacaoTransferenciaGrupoBem recuperarComDependenciasAqruivo(Object id) {
        EfetivacaoTransferenciaGrupoBem lote = super.recuperar(id);
        if (lote.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(lote.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return lote;
    }

    private void atualizarItemSolicitacao(AssistenteMovimentacaoBens assistente, EfetivacaoTransferenciaGrupoBem entity) {
        assistente.configurarInicializacao("Finalizando Evento da Solicitação de Transferência Grupo Patrimonial...", assistente.getBensMovimentadosVo().size());
        assistente.setTotal(assistente.getBensMovimentadosVo().size());

        SituacaoEventoBem situacaoEventoBem = assistente.getSituacaoMovimento().isRecusado() ? SituacaoEventoBem.RECUSADO : SituacaoEventoBem.FINALIZADO;
        for (BemVo bemVo : assistente.getBensMovimentadosVo()) {
            ItemSolicitacaoTransferenciaGrupoBem itemSol = em.find(ItemSolicitacaoTransferenciaGrupoBem.class, bemVo.getIdEventoBem());
            itemSol.setSituacaoEventoBem(situacaoEventoBem);
            em.merge(itemSol);
            assistente.conta();
        }
        em.merge(entity.getSolicitacao());
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AssistenteMovimentacaoBens salvarEfetivacao(AssistenteMovimentacaoBens assistente) {
        EfetivacaoTransferenciaGrupoBem entity = (EfetivacaoTransferenciaGrupoBem) assistente.getSelecionado();
        assistente.configurarInicializacao("Inicializando Processo...", 0);
        try {
            atualizarItemSolicitacao(assistente, entity);
            boolean solicitacaiAceita = entity.getSolicitacao().getSituacao().isFinalizado();
            if (entity.getNumero() == null) {
                entity.setNumero(singletonGeradorCodigo.getProximoCodigo(EfetivacaoTransferenciaGrupoBem.class, "numero"));
            }
            entity = em.merge(entity);

            assistente.configurarInicializacao("Transferindo Grupo Patrimonial...", assistente.getBensMovimentadosVo().size());
            assistente.setSelecionado(entity);

            List<EventoBem> eventosContabilizacao = Lists.newArrayList();
            if (solicitacaiAceita) {
                criarEventosTransferencia(assistente, eventosContabilizacao);
                integradorPatrimonialContabilFacade.transferirGrupoBens(eventosContabilizacao, assistente);
            }
            desbloquearBens(assistente);
            assistente.configurarInicializacao("Finalizando Processo...", 0);
            assistente.setSelecionado(entity);
        } catch (Exception ex) {
            logger.error("Erro ao salvar efetivação de trasnferênci grupo bem. ", ex);
            throw ex;
        }
        return assistente;
    }

    private void desbloquearBens(AssistenteMovimentacaoBens assistente) {
        List<Number> bensBloqueados = Lists.newArrayList();
        for (BemVo bemVo : assistente.getBensMovimentadosVo()) {
            bensBloqueados.add(bemVo.getId());
        }
        configMovimentacaoBemFacade.desbloquearBens(assistente.getConfigMovimentacaoBem(), bensBloqueados);
    }

    private void criarEventosTransferencia(AssistenteMovimentacaoBens assistente, List<EventoBem> eventosContabilizacao) {
        for (BemVo bemVo : assistente.getBensMovimentadosVo()) {
            Bem bem = bemVo.getBem();
            if (bemVo.getObjetoCompra() != null) {
                bem.setObjetoCompra(bemVo.getObjetoCompra());
                bem = em.merge(bem);
            }

            EstadoBem estadoIncialTransf = bemFacade.recuperarUltimoEstadoDoBem(bem);
            EstadoBem estadoResultante;
            EventoBem ultimoEvento;

            ItemEfetivacaoTransferenciaGrupoBem itemOriginalConcedido = criarEventoConcedido(assistente, estadoIncialTransf, TipoEventoBem.TRANSFERENCIA_GRUPO_PATRIMONIAL_ORIGINAL_CONCEDIDA, bem, estadoIncialTransf.getValorOriginal());
            estadoResultante = itemOriginalConcedido.getEstadoResultante();

            if (estadoIncialTransf.hasValorAcumuladoDepreciacao()) {
                ItemEfetivacaoTransferenciaGrupoBem itemDepreciacaoConcedido = criarEventoConcedido(assistente, estadoResultante, TipoEventoBem.TRANSFERENCIA_GRUPO_PATRIMONIAL_DEPRECIACAO_CONCEDIDA, bem, estadoResultante.getValorAcumuladoDaDepreciacao());
                estadoResultante = itemDepreciacaoConcedido.getEstadoResultante();
            }
            if (estadoIncialTransf.hasValorAcumuladoAmortizacao()) {
                ItemEfetivacaoTransferenciaGrupoBem itemAmortizacaoConcedido = criarEventoConcedido(assistente, estadoResultante, TipoEventoBem.TRANSFERENCIA_GRUPO_PATRIMONIAL_AMORTIZACAO_CONCEDIDA, bem, estadoResultante.getValorAcumuladoDaAmortizacao());
                estadoResultante = itemAmortizacaoConcedido.getEstadoResultante();
            }
            if (estadoIncialTransf.hasValorAcumuladoAjuste()) {
                ItemEfetivacaoTransferenciaGrupoBem itemAjusteConcedido = criarEventoConcedido(assistente, estadoResultante, TipoEventoBem.TRANSFERENCIA_GRUPO_PATRIMONIAL_AJUSTE_CONCEDIDA, bem, estadoResultante.getValorAcumuladoDeAjuste());
                estadoResultante = itemAjusteConcedido.getEstadoResultante();
            }

            ItemEfetivacaoTransferenciaGrupoBem itemOriginalRecebido = criarEventoRecebido(assistente, estadoResultante, TipoEventoBem.TRANSFERENCIA_GRUPO_PATRIMONIAL_ORIGINAL_RECEBIDA, bem, estadoIncialTransf.getValorOriginal());
            estadoResultante = itemOriginalRecebido.getEstadoResultante();
            ultimoEvento = itemOriginalRecebido;

            if (estadoIncialTransf.hasValorAcumuladoDepreciacao()) {
                ItemEfetivacaoTransferenciaGrupoBem itemDeprecicaoRecebido = criarEventoRecebido(assistente, estadoResultante, TipoEventoBem.TRANSFERENCIA_GRUPO_PATRIMONIAL_DEPRECIACAO_RECEBIDA, bem, estadoIncialTransf.getValorAcumuladoDaDepreciacao());
                estadoResultante = itemDeprecicaoRecebido.getEstadoResultante();
                ultimoEvento = itemDeprecicaoRecebido;
            }
            if (estadoIncialTransf.hasValorAcumuladoAmortizacao()) {
                ItemEfetivacaoTransferenciaGrupoBem itemAmortizacaoRecebido = criarEventoRecebido(assistente, estadoResultante, TipoEventoBem.TRANSFERENCIA_GRUPO_PATRIMONIAL_AMORTIZACAO_RECEBIDA, bem, estadoIncialTransf.getValorAcumuladoDaAmortizacao());
                estadoResultante = itemAmortizacaoRecebido.getEstadoResultante();
                ultimoEvento = itemAmortizacaoRecebido;
            }
            if (estadoIncialTransf.hasValorAcumuladoAjuste()) {
                ultimoEvento = criarEventoRecebido(assistente, estadoResultante, TipoEventoBem.TRANSFERENCIA_GRUPO_PATRIMONIAL_AJUSTE_RECEBIDA, bem, estadoIncialTransf.getValorAcumuladoDaAmortizacao());
            }
            eventosContabilizacao.add(ultimoEvento);

            if (bemVo.getObjetoCompra() != null) {
                bem.setObjetoCompra(bemVo.getObjetoCompra());
                em.merge(bem);
            }
            assistente.conta();
        }
    }

    private ItemEfetivacaoTransferenciaGrupoBem criarEventoConcedido(AssistenteMovimentacaoBens assistente, EstadoBem ultimoEstado, TipoEventoBem tipoEvento, Bem bem, BigDecimal valorLancamento) {
        EfetivacaoTransferenciaGrupoBem entity = (EfetivacaoTransferenciaGrupoBem) assistente.getSelecionado();

        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado);
        atribuirValorEstadoBem(tipoEvento, novoEstado, BigDecimal.ZERO);
        novoEstado = em.merge(novoEstado);

        ItemEfetivacaoTransferenciaGrupoBem novoItem = new ItemEfetivacaoTransferenciaGrupoBem(tipoEvento, TipoOperacao.DEBITO, entity);
        novoItem.setEfetivacao(entity);
        novoItem.setBem(bem);
        novoItem.setEstadoInicial(ultimoEstado);
        novoItem.setEstadoResultante(novoEstado);
        novoItem.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        novoItem.setValorDoLancamento(valorLancamento);
        novoItem.setDataLancamento(DataUtil.somaPeriodo(getDataLacamento(assistente), 1, TipoPrazo.SEGUNDOS));
        novoItem.setDataOperacao(DataUtil.somaPeriodo(novoItem.getDataOperacao(), 3, TipoPrazo.SEGUNDOS));
        return em.merge(novoItem);
    }

    private ItemEfetivacaoTransferenciaGrupoBem criarEventoRecebido(AssistenteMovimentacaoBens assistente, EstadoBem estadoResultante, TipoEventoBem tipoEvento, Bem bem, BigDecimal valorLancamento) {
        EfetivacaoTransferenciaGrupoBem entity = (EfetivacaoTransferenciaGrupoBem) assistente.getSelecionado();

        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoResultante);
        atribuirValorEstadoBem(tipoEvento, novoEstado, valorLancamento);
        novoEstado.setGrupoBem(entity.getSolicitacao().getGrupoBemDestino());
        novoEstado.setGrupoObjetoCompra(bem.getObjetoCompra().getGrupoObjetoCompra());
        novoEstado = em.merge(novoEstado);

        ItemEfetivacaoTransferenciaGrupoBem novoItem = new ItemEfetivacaoTransferenciaGrupoBem(tipoEvento, TipoOperacao.CREDITO, entity);
        novoItem.setBem(bem);
        novoItem.setEstadoInicial(estadoResultante);
        novoItem.setEstadoResultante(novoEstado);
        novoItem.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        novoItem.setValorDoLancamento(valorLancamento);
        novoItem.setDataLancamento(DataUtil.somaPeriodo(getDataLacamento(assistente), 4, TipoPrazo.SEGUNDOS));
        novoItem.setDataOperacao(DataUtil.somaPeriodo(novoItem.getDataOperacao(), 5, TipoPrazo.SEGUNDOS));
        return em.merge(novoItem);
    }

    private void atribuirValorEstadoBem(TipoEventoBem tipoEventoBem, EstadoBem estadoBem, BigDecimal valorLancamento) {
        switch (tipoEventoBem) {
            case TRANSFERENCIA_GRUPO_PATRIMONIAL_ORIGINAL_CONCEDIDA:
            case TRANSFERENCIA_GRUPO_PATRIMONIAL_ORIGINAL_RECEBIDA:
                estadoBem.setValorOriginal(valorLancamento);
                break;
            case TRANSFERENCIA_GRUPO_PATRIMONIAL_DEPRECIACAO_CONCEDIDA:
            case TRANSFERENCIA_GRUPO_PATRIMONIAL_DEPRECIACAO_RECEBIDA:
                estadoBem.setValorAcumuladoDaDepreciacao(valorLancamento);
                break;
            case TRANSFERENCIA_GRUPO_PATRIMONIAL_AMORTIZACAO_CONCEDIDA:
            case TRANSFERENCIA_GRUPO_PATRIMONIAL_AMORTIZACAO_RECEBIDA:
                estadoBem.setValorAcumuladoDaAmortizacao(valorLancamento);
                break;
            case TRANSFERENCIA_GRUPO_PATRIMONIAL_AJUSTE_CONCEDIDA:
            case TRANSFERENCIA_GRUPO_PATRIMONIAL_AJUSTE_RECEBIDA:
                estadoBem.setValorAcumuladoDeAjuste(valorLancamento);
                break;
        }
    }

    private static Date getDataLacamento(AssistenteMovimentacaoBens assistente) {
        return DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public void setSingletonBloqueioPatrimonio(SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio) {
        this.singletonBloqueioPatrimonio = singletonBloqueioPatrimonio;
    }

    public SolicitacaoTransferenciaGrupoBemFacade getSolicitacaoTransferenciaGrupoBemFacade() {
        return solicitacaoTransferenciaGrupoBemFacade;
    }

    public PesquisaBemFacade getPesquisaBemFacade() {
        return pesquisaBemFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }
}
