package br.com.webpublico.pncp.dto;

import java.math.BigDecimal;


public class ItemCompraConsultaDTO {

    private Integer numeroLote;
    private Integer numeroItem;
    private String descricao;
    private String unidadeMedida;
    private BigDecimal quantidade;
    private BigDecimal valorUnitarioEstimado;
    private BigDecimal valorTotal;

    public Integer getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(Integer numeroLote) {
        this.numeroLote = numeroLote;
    }

    public Integer getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Integer numeroItem) {
        this.numeroItem = numeroItem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitarioEstimado() {
        return valorUnitarioEstimado;
    }

    public void setValorUnitarioEstimado(BigDecimal valorUnitarioEstimado) {
        this.valorUnitarioEstimado = valorUnitarioEstimado;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
