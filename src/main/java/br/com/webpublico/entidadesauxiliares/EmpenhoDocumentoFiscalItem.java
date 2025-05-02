package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class EmpenhoDocumentoFiscalItem {
    private Long idEmpenho;
    private Integer numeroItem;
    private String descricaoItem;
    private String grupoItem;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;

    public Long getIdEmpenho() {
        return idEmpenho;
    }

    public void setIdEmpenho(Long idEmpenho) {
        this.idEmpenho = idEmpenho;
    }

    public Integer getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Integer numeroItem) {
        this.numeroItem = numeroItem;
    }

    public String getDescricaoItem() {
        return descricaoItem;
    }

    public void setDescricaoItem(String descricaoItem) {
        this.descricaoItem = descricaoItem;
    }

    public String getGrupoItem() {
        return grupoItem;
    }

    public void setGrupoItem(String grupoItem) {
        this.grupoItem = grupoItem;
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

}
