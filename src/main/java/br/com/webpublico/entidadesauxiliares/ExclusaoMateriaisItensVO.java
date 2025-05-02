package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class ExclusaoMateriaisItensVO {

    private Long numero;
    private Long idMaterial;
    private String material;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal quantidadeEstoqueAtual;
    private BigDecimal valorEstoqueAtual;

    public ExclusaoMateriaisItensVO() {
        quantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
        quantidadeEstoqueAtual = BigDecimal.ZERO;
        valorEstoqueAtual = BigDecimal.ZERO;
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
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

    public BigDecimal getQuantidadeEstoqueAtual() {
        return quantidadeEstoqueAtual;
    }

    public void setQuantidadeEstoqueAtual(BigDecimal quantidadeEstoqueAtual) {
        this.quantidadeEstoqueAtual = quantidadeEstoqueAtual;
    }

    public BigDecimal getValorEstoqueAtual() {
        return valorEstoqueAtual;
    }

    public void setValorEstoqueAtual(BigDecimal valorEstoqueAtual) {
        this.valorEstoqueAtual = valorEstoqueAtual;
    }

    public boolean hasEstoqueNegativo() {
        return valorTotal != null && valorEstoqueAtual != null && valorTotal.compareTo(valorEstoqueAtual) > 0;
    }
}
