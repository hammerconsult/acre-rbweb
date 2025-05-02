package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemConversaoUnidadeMedidaMaterial;
import br.com.webpublico.entidades.ItemEntradaMaterial;
import br.com.webpublico.entidades.ItemEntradaMaterialEstorno;
import br.com.webpublico.entidades.ItemSaidaMaterial;

import java.math.BigDecimal;
import java.util.Date;

public class ItemReprocessamentoEstoque {

    private Long idItem;
    private Date dataMovimento;
    private TipoMovimento tipoMovimento;
    private String material;
    private String descricaoMovimento;
    private String localEstoque;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private ItemEntradaMaterial itemEntradaMaterial;
    private ItemEntradaMaterialEstorno itemEntradaMaterialEstorno;
    private ItemSaidaMaterial itemSaidaMaterial;
    private ItemConversaoUnidadeMedidaMaterial itemConversao;

    public ItemReprocessamentoEstoque() {
    }

    public ItemReprocessamentoEstoque(ItemEntradaMaterial itemEntrada) {
        setIdItem(itemEntrada.getId());
        setItemEntradaMaterial(itemEntrada);
        setTipoMovimento(TipoMovimento.ENTRADA);
        setDataMovimento(itemEntrada.getDataMovimento());
        setMaterial(itemEntrada.getMaterial().toStringAutoComplete());
        setDescricaoMovimento(itemEntrada.getDescricaoDaOperacao());
        setQuantidade(itemEntrada.getQuantidade());
        setValorUnitario(itemEntrada.getValorUnitario());
        setValorTotal(itemEntrada.getValorTotal());
    }

    public ItemReprocessamentoEstoque(ItemEntradaMaterialEstorno itemEntEst) {
        setIdItem(itemEntEst.getId());
        setItemEntradaMaterialEstorno(itemEntEst);
        setTipoMovimento(TipoMovimento.ESTORNO_ENTRADA);
        setDataMovimento(itemEntEst.getDataMovimento());
        setMaterial(itemEntEst.getMaterial().toStringAutoComplete());
        setDescricaoMovimento(itemEntEst.getDescricaoDaOperacao());
        setQuantidade(itemEntEst.getQuantidade());
        setValorUnitario(itemEntEst.getValorUnitario());
        setValorTotal(itemEntEst.getValorTotal());
    }

    public ItemReprocessamentoEstoque(ItemSaidaMaterial itemSaida) {
        setIdItem(itemSaida.getId());
        setItemSaidaMaterial(itemSaida);
        setTipoMovimento(TipoMovimento.SAIDA);
        setDataMovimento(itemSaida.getDataMovimento());
        setMaterial(itemSaida.getMaterial().toStringAutoComplete());
        setDescricaoMovimento(itemSaida.getDescricaoDaOperacao());
        setQuantidade(itemSaida.getQuantidade());
        setValorUnitario(itemSaida.getValorUnitario());
        setValorTotal(itemSaida.getValorTotal());
    }

    public ItemReprocessamentoEstoque(TipoMovimento tipoMovimento, ItemConversaoUnidadeMedidaMaterial itemConversao) {
        setIdItem(itemConversao.getId());
        setItemConversao(itemConversao);
        setTipoMovimento(tipoMovimento);
        setDataMovimento(itemConversao.getConversaoUnidadeMedida().getDataMovimento());
        setMaterial(tipoMovimento.isConversaoEntrada() ? itemConversao.getMaterialEntrada().toStringAutoComplete() : itemConversao.getMaterialSaida().toStringAutoComplete());

        Long numeroConversao = itemConversao.getConversaoUnidadeMedida().getNumero();
        setDescricaoMovimento(tipoMovimento.isConversaoEntrada() ? "Entrada por Conversão Unidade de Medida n° " + numeroConversao : "Saída por Conversão Unidade de Medida n° " + numeroConversao);

        setQuantidade(tipoMovimento.isConversaoEntrada() ? itemConversao.getQuantidadeEntrada() : itemConversao.getQuantidadeSaida());
        setValorUnitario(tipoMovimento.isConversaoEntrada() ? itemConversao.getValorUnitarioEntrada() : itemConversao.getValorUnitarioSaida());
        setValorTotal(tipoMovimento.isConversaoEntrada() ? itemConversao.getValorTotalEntrada() : itemConversao.getValorTotalSaida());
    }

    public String getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(String localEstoque) {
        this.localEstoque = localEstoque;
    }

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public TipoMovimento getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimento tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public ItemEntradaMaterial getItemEntradaMaterial() {
        return itemEntradaMaterial;
    }

    public void setItemEntradaMaterial(ItemEntradaMaterial itemEntradaMaterial) {
        this.itemEntradaMaterial = itemEntradaMaterial;
    }

    public ItemEntradaMaterialEstorno getItemEntradaMaterialEstorno() {
        return itemEntradaMaterialEstorno;
    }

    public void setItemEntradaMaterialEstorno(ItemEntradaMaterialEstorno itemEntradaMaterialEstorno) {
        this.itemEntradaMaterialEstorno = itemEntradaMaterialEstorno;
    }

    public ItemSaidaMaterial getItemSaidaMaterial() {
        return itemSaidaMaterial;
    }

    public void setItemSaidaMaterial(ItemSaidaMaterial itemSaidaMaterial) {
        this.itemSaidaMaterial = itemSaidaMaterial;
    }

    public ItemConversaoUnidadeMedidaMaterial getItemConversao() {
        return itemConversao;
    }

    public void setItemConversao(ItemConversaoUnidadeMedidaMaterial itemConversao) {
        this.itemConversao = itemConversao;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDescricaoMovimento() {
        return descricaoMovimento;
    }

    public void setDescricaoMovimento(String descricaoMovimento) {
        this.descricaoMovimento = descricaoMovimento;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public enum TipoMovimento {
        ENTRADA, ESTORNO_ENTRADA, SAIDA, CONVERSAO, CONVERSAO_ENTRADA, CONVERSAO_SAIDA;

        public boolean isEntrada() {
            return TipoMovimento.ENTRADA.equals(this);
        }

        public boolean isEstornoEntrada() {
            return TipoMovimento.ESTORNO_ENTRADA.equals(this);
        }

        public boolean isSaida() {
            return TipoMovimento.SAIDA.equals(this);
        }

        public boolean isConversao() {
            return TipoMovimento.CONVERSAO.equals(this);
        }

        public boolean isConversaoEntrada() {
            return TipoMovimento.CONVERSAO_ENTRADA.equals(this);
        }

        public boolean isConversaoSaida() {
            return TipoMovimento.CONVERSAO_SAIDA.equals(this);
        }
    }
}
