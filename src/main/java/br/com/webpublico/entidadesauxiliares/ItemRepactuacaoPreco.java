package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemCertame;
import br.com.webpublico.entidades.ItemPregao;
import br.com.webpublico.entidades.ItemProcessoDeCompra;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;

import java.math.BigDecimal;

public class ItemRepactuacaoPreco implements Comparable<ItemRepactuacaoPreco> {

    private BigDecimal quantidadeLicitacao;
    private BigDecimal quantidadeRestante;
    private BigDecimal valorLicitacao;
    private BigDecimal valorRestante;
    private TipoControleLicitacao tipoControle;
    private BigDecimal precoAtual;
    private BigDecimal precoProposto;
    private ItemPregao itemPregao;
    private ItemCertame itemCertame;
    private ObjetoCompra objetoCompra;
    private Integer numero;
    private String descricaoComplementar;
    private SituacaoPrecoItem situacaoPrecoItem;

    public ItemRepactuacaoPreco(ItemPregao itemPregao) {
        this.quantidadeLicitacao = itemPregao.getQuantidade();
        this.valorLicitacao = itemPregao.getValorTotalItem();
        this.precoAtual = itemPregao.getValor();
        this.objetoCompra = itemPregao.getObjetoCompra();
        this.itemPregao = itemPregao;
        this.numero = itemPregao.getNumeroItem();
        this.descricaoComplementar = itemPregao.getDescricaoComplementar();
        this.tipoControle = getTipoControleProcesso(itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra());
        this.situacaoPrecoItem = SituacaoPrecoItem.NAO_INFORMADO;

    }

    public ItemRepactuacaoPreco(ItemCertame itemCertame) {
        this.quantidadeLicitacao = itemCertame.getQuantidadeItem();
        this.valorLicitacao = itemCertame.getValorTotal();
        this.precoAtual = itemCertame.getPrecoItem();
        this.objetoCompra = itemCertame.getObjetoCompra();
        this.itemCertame = itemCertame;
        this.numero = itemCertame.getNumeroItem();
        this.descricaoComplementar = itemCertame.getDescricaoProdutoItem();
        this.tipoControle = getTipoControleProcesso(itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra());
        this.situacaoPrecoItem = SituacaoPrecoItem.NAO_INFORMADO;
    }

    public BigDecimal getValorLicitacao() {
        return valorLicitacao;
    }

    public void setValorLicitacao(BigDecimal valorLicitacao) {
        this.valorLicitacao = valorLicitacao;
    }

    public BigDecimal getValorRestante() {
        return valorRestante;
    }

    public void setValorRestante(BigDecimal valorRestante) {
        this.valorRestante = valorRestante;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public BigDecimal getQuantidadeRestante() {
        return quantidadeRestante;
    }

    public void setQuantidadeRestante(BigDecimal quantidadeRestante) {
        this.quantidadeRestante = quantidadeRestante;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public void setQuantidadeLicitacao(BigDecimal quantidadeLicitacao) {
        this.quantidadeLicitacao = quantidadeLicitacao;
    }

    public BigDecimal getQuantidadeLicitacao() {
        return quantidadeLicitacao;
    }

    public BigDecimal getPrecoAtual() {
        return precoAtual;
    }

    public void setPrecoAtual(BigDecimal precoAtual) {
        this.precoAtual = precoAtual;
    }

    public BigDecimal getPrecoProposto() {
        return precoProposto;
    }

    public void setPrecoProposto(BigDecimal precoProposto) {
        this.precoProposto = precoProposto;
    }

    public ItemPregao getItemPregao() {
        return itemPregao;
    }

    public void setItemPregao(ItemPregao itemPregao) {
        this.itemPregao = itemPregao;
    }

    public ItemCertame getItemCertame() {
        return itemCertame;
    }

    public void setItemCertame(ItemCertame itemCertame) {
        this.itemCertame = itemCertame;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public SituacaoPrecoItem getSituacaoPrecoItem() {
        return situacaoPrecoItem;
    }

    public void setSituacaoPrecoItem(SituacaoPrecoItem situacaoPrecoItem) {
        this.situacaoPrecoItem = situacaoPrecoItem;
    }

    public boolean isPregao() {
        return itemPregao != null;
    }

    public void atribuirQuantidadeOrValorRestante(BigDecimal quantidadeOrValorUtilizado) {
        if (tipoControle.isTipoControlePorQuantidade()) {
            setQuantidadeRestante(quantidadeLicitacao.subtract(quantidadeOrValorUtilizado));
        }
        setValorRestante(valorLicitacao.subtract(quantidadeOrValorUtilizado));
    }

    private TipoControleLicitacao getTipoControleProcesso(ItemProcessoDeCompra itemProcessoCompra) {
        try {
            return itemProcessoCompra.getItemSolicitacaoMaterial().getItemCotacao().getTipoControle();
        } catch (Exception e) {
            return TipoControleLicitacao.QUANTIDADE;
        }
    }

    public boolean hasPrecoProposto() {
        return precoProposto != null && precoProposto.compareTo(BigDecimal.ZERO) > 0;
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitario() {
        try {
            if (isPregao()) {
                return itemPregao.getMascaraValorUnitario();
            }
            return itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getUnidadeMedida().getMascaraValorUnitario();
        } catch (Exception ex) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
    }

    @Override
    public int compareTo(ItemRepactuacaoPreco o) {
        if (getNumero() != null) {
            return getNumero().compareTo(o.getNumero());
        }
        return 0;
    }

    public enum SituacaoPrecoItem {
        DIMINUIU("Diminuiu"),
        AUMENTOU("Aumentou"),
        PERMANECEU("Permaneceu"),
        NAO_INFORMADO("Não Informado");
        private String descricao;

        SituacaoPrecoItem(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
