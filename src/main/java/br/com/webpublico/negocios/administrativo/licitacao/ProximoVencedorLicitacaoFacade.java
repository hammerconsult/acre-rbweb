package br.com.webpublico.negocios.administrativo.licitacao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.licitacao.ItemPregaoLanceVencedor;
import br.com.webpublico.entidades.administrativo.licitacao.ProximoVencedorLicitacao;
import br.com.webpublico.entidades.administrativo.licitacao.ProximoVencedorLicitacaoItem;
import br.com.webpublico.entidadesauxiliares.ProximoVencedorLicitacaoItemVo;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.administrativo.ParticipanteLicitacaoFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ProximoVencedorLicitacaoFacade extends AbstractFacade<ProximoVencedorLicitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PropostaFornecedorFacade propostaFornecedorFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private ItemPregaoFacade itemPregaoFacade;
    @EJB
    private PregaoFacade pregaoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ParticipanteLicitacaoFacade participanteLicitacaoFacade;
    @EJB
    private StatusFornecedorLicitacaoFacade statusFornecedorLicitacaoFacade;

    public ProximoVencedorLicitacaoFacade() {
        super(ProximoVencedorLicitacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public PropostaFornecedorFacade getPropostaFornecedorFacade() {
        return propostaFornecedorFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public ItemPregaoFacade getItemPregaoFacade() {
        return itemPregaoFacade;
    }

    public PregaoFacade getPregaoFacade() {
        return pregaoFacade;
    }

    @Override
    public ProximoVencedorLicitacao recuperar(Object id) {
        ProximoVencedorLicitacao entity = super.recuperar(id);
        Hibernate.initialize(entity.getItens());
        return entity;
    }

    public ProximoVencedorLicitacao salvarRetornando(ProximoVencedorLicitacao entity, List<ProximoVencedorLicitacaoItemVo> itens, Pregao pregao) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(ProximoVencedorLicitacao.class, "numero"));
        }
        movimentarDadosPregao(itens);
        if (entity.getLicitacao().getTipoApuracao().isPorLote()) {
            pregaoFacade.salvarPregaoPorLote(pregao);
            em.flush();
        }
        adjudicarAndHomologarProximoVencedor(itens);
        criarProximoVencedorLicitacaoItem(entity, itens);
        return super.salvarRetornando(entity);
    }

    private void criarProximoVencedorLicitacaoItem(ProximoVencedorLicitacao entity, List<ProximoVencedorLicitacaoItemVo> itens) {
        for (ProximoVencedorLicitacaoItemVo itemVo : itens) {
            if (itemVo.hasItemSelecionado()) {
                ProximoVencedorLicitacaoItem item = new ProximoVencedorLicitacaoItem();
                item.setProximoVencedor(itemVo.getProximoVencedor().getPropostaFornecedor());
                item.setProximoVencedorLicitacao(entity);
                item.setItemPregao(itemVo.getItemPregao());
                item.setValorProximoLance(itemVo.getValorProximoLance());
                item.setValorLanceAtual(itemVo.getValorLanceAtual());
                entity.getItens().add(item);
            }
        }
    }

    private void adjudicarAndHomologarProximoVencedor(List<ProximoVencedorLicitacaoItemVo> itens) {
        HashMap<LicitacaoFornecedor, List<ProximoVencedorLicitacaoItemVo>> map = criarMapItensPorLicitacaoFornecedor(itens);
        for (Map.Entry<LicitacaoFornecedor, List<ProximoVencedorLicitacaoItemVo>> entry : map.entrySet()) {
            criarStatusFornecedorLicitacao(itens, entry.getKey(), TipoSituacaoFornecedorLicitacao.ADJUDICADA, SituacaoItemProcessoDeCompra.ADJUDICADO);
            criarStatusFornecedorLicitacao(itens, entry.getKey(), TipoSituacaoFornecedorLicitacao.HOMOLOGADA, SituacaoItemProcessoDeCompra.HOMOLOGADO);
        }
    }

    private void criarStatusFornecedorLicitacao(List<ProximoVencedorLicitacaoItemVo> itens, LicitacaoFornecedor licitacaoFornecedor, TipoSituacaoFornecedorLicitacao adjudicada, SituacaoItemProcessoDeCompra adjudicado) {
        StatusFornecedorLicitacao statusFornecedor = new StatusFornecedorLicitacao();
        statusFornecedor.setLicitacaoFornecedor(licitacaoFornecedor);
        statusFornecedor.setNumero(statusFornecedorLicitacaoFacade.recuperaUltimoNumero(licitacaoFornecedor.getLicitacao()));
        statusFornecedor.setDataSituacao(sistemaFacade.getDataOperacao());
        statusFornecedor.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        statusFornecedor.setMotivo(getMotivoStatusFornecedor(adjudicada));
        statusFornecedor.setTipoSituacao(adjudicada);

        for (ProximoVencedorLicitacaoItemVo item : itens) {
            if (item.hasItemSelecionado()) {
                if (item.getItemPregao().getItemPregaoItemProcesso() != null) {
                    ItemProcessoDeCompra itemProcessoCompra = homologarItemProcessoCompra(item.getItemPregao().getItemPregaoItemProcesso().getItemProcessoDeCompra());

                    ItemStatusFornecedorLicitacao novoItemStatus = new ItemStatusFornecedorLicitacao();
                    novoItemStatus.setStatusFornecedorLicitacao(statusFornecedor);
                    novoItemStatus.setSituacao(adjudicado);
                    novoItemStatus.setItemProcessoCompra(itemProcessoCompra);
                    novoItemStatus.setValorUnitario(item.getValorProximoLance());
                    statusFornecedor.getItens().add(novoItemStatus);
                } else {
                    List<ItemPregaoLoteItemProcesso> itensLote = pregaoFacade.buscarItensPregaoLoteItemProcesso(item.getItemPregao());
                    for (ItemPregaoLoteItemProcesso itemPregaoLote : itensLote) {
                        ItemProcessoDeCompra itemProcessoCompra = homologarItemProcessoCompra(itemPregaoLote.getItemProcessoDeCompra());

                        ItemStatusFornecedorLicitacao novoItemStatus = new ItemStatusFornecedorLicitacao();
                        novoItemStatus.setStatusFornecedorLicitacao(statusFornecedor);
                        novoItemStatus.setSituacao(adjudicado);
                        novoItemStatus.setItemProcessoCompra(itemProcessoCompra);
                        novoItemStatus.setValorUnitario(itemPregaoLote.getValor());
                        statusFornecedor.getItens().add(novoItemStatus);
                    }
                }
            }
        }
        em.merge(statusFornecedor);

        licitacaoFornecedor.setTipoClassificacaoFornecedor(TipoClassificacaoFornecedor.HABILITADO);
        em.merge(licitacaoFornecedor);
    }

    private ItemProcessoDeCompra homologarItemProcessoCompra(ItemProcessoDeCompra item) {
        item.setSituacaoItemProcessoDeCompra(SituacaoItemProcessoDeCompra.HOMOLOGADO);
        return em.merge(item);
    }

    private HashMap<LicitacaoFornecedor, List<ProximoVencedorLicitacaoItemVo>> criarMapItensPorLicitacaoFornecedor(List<ProximoVencedorLicitacaoItemVo> itens) {
        HashMap<LicitacaoFornecedor, List<ProximoVencedorLicitacaoItemVo>> map = new HashMap<>();
        for (ProximoVencedorLicitacaoItemVo item : itens) {
            if (item.hasItemSelecionado()) {
                LicitacaoFornecedor licitacaoFornecedor = item.getLicitacaoFornecedor();
                if (!map.containsKey(licitacaoFornecedor)) {
                    map.put(licitacaoFornecedor, Lists.newArrayList(item));
                } else {
                    map.get(licitacaoFornecedor).add(item);
                }
            }
        }
        return map;
    }

    private String getMotivoStatusFornecedor(TipoSituacaoFornecedorLicitacao tipo) {
        return "Registro de " + tipo.getDescricaoFuncional() + " da empresa vencedora gerado automaticamente pelo sistema no processo de próximo vencedor da licitação.";
    }

    public LicitacaoFornecedor getLicitacaoFornecedor(Licitacao licitacao, PropostaFornecedor propostaFornecedor) {
        LicitacaoFornecedor licitacaoFornecedor = participanteLicitacaoFacade.buscarLicitacaoFornecedor(licitacao, propostaFornecedor.getFornecedor());
        if (licitacaoFornecedor == null) {
            throw new ExcecaoNegocioGenerica("Participante não encontrado para a proposta " + propostaFornecedor.getFornecedor() + ".");
        }
        return licitacaoFornecedor;
    }

    private void movimentarDadosPregao(List<ProximoVencedorLicitacaoItemVo> itens) {
        for (ProximoVencedorLicitacaoItemVo item : itens) {
            if (item.hasItemSelecionado()) {
                atualizarItemVencedorAtual(item.getItemPregao());
                gerarItemPregaoLanceVencedorParaProximoVencedor(item);
            }
        }
    }

    private void gerarItemPregaoLanceVencedorParaProximoVencedor(ProximoVencedorLicitacaoItemVo item) {
        boolean isMaiorDesconto = item.getProximoVencedorLicitacao().getLicitacao().getTipoAvaliacao().isMaiorDesconto();

        ItemPregaoLanceVencedor iplv = new ItemPregaoLanceVencedor();
        iplv.setItemPregao(item.getItemPregao());
        iplv.setLancePregao(item.getLanceVencedor());
        iplv.setStatus(StatusLancePregao.VENCEDOR);
        iplv.setOrigem(OrigemItemPregaoLanceVencedor.PROXIMO_VENCEDOR_LICITACAO);
        iplv.setValor(!isMaiorDesconto ? item.getValorProximoLance() : BigDecimal.ZERO);
        iplv.setPercentualDesconto(isMaiorDesconto ? item.getValorProximoLance() : BigDecimal.ZERO);
        iplv = em.merge(iplv);

        ItemPregao itemPregao = item.getItemPregao();
        itemPregao.setItemPregaoLanceVencedor(iplv);
        itemPregao = em.merge(itemPregao);
        item.setItemPregao(itemPregao);
    }

    private void atualizarItemVencedorAtual(ItemPregao itemPregao) {
        ItemPregaoLanceVencedor iplv = itemPregao.getItemPregaoLanceVencedor();
        iplv.setStatus(StatusLancePregao.DECLINADO);
        em.merge(iplv);
    }
}
