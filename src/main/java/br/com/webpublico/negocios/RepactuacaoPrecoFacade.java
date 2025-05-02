package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.licitacao.ItemPregaoLanceVencedor;
import br.com.webpublico.entidadesauxiliares.ItemRepactuacaoPreco;
import br.com.webpublico.enums.OrigemItemPregaoLanceVencedor;
import br.com.webpublico.enums.SituacaoItemCertame;
import br.com.webpublico.enums.StatusLancePregao;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 14/04/14
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class RepactuacaoPrecoFacade extends AbstractFacade<RepactuacaoPreco> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private PregaoFacade pregaoFacade;
    @EJB
    private CertameFacade certameFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private StatusFornecedorLicitacaoFacade statusFornecedorLicitacaoFacade;
    @EJB
    private ItemContratoFacade itemContratoFacade;
    @EJB
    private ItemPregaoFacade itemPregaoFacade;

    public RepactuacaoPrecoFacade() {
        super(RepactuacaoPreco.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AtaRegistroPrecoFacade getAtaRegistroPrecoFacade() {
        return ataRegistroPrecoFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public PregaoFacade getPregaoFacade() {
        return pregaoFacade;
    }

    public CertameFacade getCertameFacade() {
        return certameFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    @Override
    public RepactuacaoPreco recuperar(Object id) {
        RepactuacaoPreco entity = em.find(RepactuacaoPreco.class, id);

        if (entity.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(entity.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        Hibernate.initialize(entity.getItens());
        return entity;
    }

    public RepactuacaoPreco salvarRetornando(RepactuacaoPreco entity, List<ItemRepactuacaoPreco> itens) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(RepactuacaoPreco.class, "numero"));
        }
        atualizarValorItemVencedor(itens, false);
        criarObjetoCompraRepactuacao(itens, entity);
        em.flush();
        return em.merge(entity);
    }

    public void remover(RepactuacaoPreco entity, List<ItemRepactuacaoPreco> itens) {
        atualizarValorItemVencedor(itens, true);
        super.remover(entity);
    }

    private void criarObjetoCompraRepactuacao(List<ItemRepactuacaoPreco> itens, RepactuacaoPreco entiy) {
        entiy.setItens(Lists.<ObjetoCompraRepactuacao>newArrayList());
        for (ItemRepactuacaoPreco item : itens) {
            if (item.hasPrecoProposto()) {
                ObjetoCompraRepactuacao ocr = new ObjetoCompraRepactuacao();
                ocr.setRepactuacaoPreco(entiy);
                ocr.setQuantidade(item.getQuantidadeLicitacao());
                ocr.setPrecoAtual(item.getPrecoAtual());
                ocr.setPrecoProposto(item.getPrecoProposto());
                ocr.setObjetoCompra(item.getObjetoCompra());
                Util.adicionarObjetoEmLista(entiy.getItens(), ocr);
            }
        }
    }

    private void atualizarValorItemVencedor(List<ItemRepactuacaoPreco> itens, boolean isEstorno) {
        for (ItemRepactuacaoPreco itemRepactuacao : itens) {
            if (itemRepactuacao.hasPrecoProposto()) {
                if (itemRepactuacao.isPregao()) {
                    ItemPregaoLanceVencedor itemVencAtual = itemRepactuacao.getItemPregao().getItemPregaoLanceVencedor();
                    if (itemVencAtual != null) {
                        criarNovoItemPregaoLanceVencedor(isEstorno, itemRepactuacao, itemVencAtual);
                        atualizarItemPregaoLanceVencedorAtual(itemVencAtual);
                    }
                } else {
                    ItemPropostaFornecedor ipf = itemRepactuacao.getItemCertame().getItemPropostaFornecedorVencedor();
                    if (ipf != null) {
                        ipf.setPreco(isEstorno ? itemRepactuacao.getPrecoAtual() : itemRepactuacao.getPrecoProposto());
                        em.merge(ipf);
                    }
                }
            }
        }
    }

    private void criarNovoItemPregaoLanceVencedor(boolean isEstorno, ItemRepactuacaoPreco itemRepactuacao, ItemPregaoLanceVencedor itemVencAtual) {
        ItemPregao itemPregao = itemRepactuacao.getItemPregao();
        ItemPregaoLanceVencedor novoItemVenc = new ItemPregaoLanceVencedor();
        novoItemVenc.setItemPregao(itemPregao);
        novoItemVenc.setLancePregao(itemVencAtual.getLancePregao());
        novoItemVenc.setStatus(StatusLancePregao.VENCEDOR);
        novoItemVenc.setOrigem(OrigemItemPregaoLanceVencedor.REPACTUACAO_PRECO);
        novoItemVenc.setValor(isEstorno ? itemRepactuacao.getPrecoAtual() : itemRepactuacao.getPrecoProposto());
        novoItemVenc = em.merge(novoItemVenc);

        itemPregao.setItemPregaoLanceVencedor(novoItemVenc);
        em.merge(itemPregao);
    }

    private void atualizarItemPregaoLanceVencedorAtual(ItemPregaoLanceVencedor itemVencAtual) {
        itemVencAtual.setStatus(StatusLancePregao.DECLINADO);
        em.merge(itemVencAtual);
    }

    public List<ItemRepactuacaoPreco> criarItensRepactuacao(RepactuacaoPreco entity) {
        List<ItemRepactuacaoPreco> itens = Lists.newArrayList();
        if (isPregao(entity)) {
            List<ItemPregao> itensVencedores = pregaoFacade.buscarItensPregaoVencedorPorFornecedor(entity.getAtaRegistroPreco().getLicitacao(), entity.getFornecedor().getEmpresa());
            for (ItemPregao itemPregao : itensVencedores) {
                ItemRepactuacaoPreco item = new ItemRepactuacaoPreco(itemPregao);
                item.atribuirQuantidadeOrValorRestante(getQtdeOrValorUtilizadoEmContrato(entity, itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra()));
                Util.adicionarObjetoEmLista(itens, item);
            }
        } else {
            Certame certame = getCertame(entity.getAtaRegistroPreco().getLicitacao());
            for (ItemCertame itemCertame : certame.getListaItemCertame()) {
                if (itemCertame.getFornecedor().equals(entity.getFornecedor().getEmpresa()) && SituacaoItemCertame.VENCEDOR.equals(itemCertame.getSituacaoItemCertame())) {
                    ItemRepactuacaoPreco item = new ItemRepactuacaoPreco(itemCertame);
                    item.atribuirQuantidadeOrValorRestante(getQtdeOrValorUtilizadoEmContrato(entity, itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra()));
                    Util.adicionarObjetoEmLista(itens, item);
                }
            }
        }
        return itens;
    }

    private boolean isPregao(RepactuacaoPreco entity) {
        return entity.getAtaRegistroPreco().getLicitacao().isPregao() || entity.getAtaRegistroPreco().getLicitacao().isRDCPregao();
    }

    public BigDecimal getQtdeOrValorUtilizadoEmContrato(RepactuacaoPreco entity, ItemProcessoDeCompra itemProcessoDeCompra) {
        return itemContratoFacade.recuperarQuantidadeOrValorUtilizadoEmOutrosContratosLicitacao(itemProcessoDeCompra, entity.getAtaRegistroPreco().getLicitacao());
    }

    public Certame getCertame(Licitacao licitacao) {
        return certameFacade.recuperarCertameDaLicitacao(licitacao);
    }
}
