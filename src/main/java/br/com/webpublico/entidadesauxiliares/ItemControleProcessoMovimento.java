package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoProcesso;
import br.com.webpublico.enums.TipoControleLicitacao;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemControleProcessoMovimento {

    private Long idItem;
    private Long idMovimento;
    private String descricao;
    private TipoProcesso tipoProcesso;
    private TipoControleLicitacao tipoControle;
    private BigDecimal quantidade;
    private BigDecimal quantidadeOriginal;
    private BigDecimal valorUnitario;
    private BigDecimal valorUnitarioOriginal;
    private BigDecimal valorTotal;
    private BigDecimal valorTotalOriginal;

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long id) {
        this.idItem = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public BigDecimal getQuantidadeOriginal() {
        return quantidadeOriginal;
    }

    public void setQuantidadeOriginal(BigDecimal quantidadeOriginal) {
        this.quantidadeOriginal = quantidadeOriginal;
    }

    public BigDecimal getValorUnitarioOriginal() {
        return valorUnitarioOriginal;
    }

    public void setValorUnitarioOriginal(BigDecimal valorUnitarioOriginal) {
        this.valorUnitarioOriginal = valorUnitarioOriginal;
    }

    public BigDecimal getValorTotalOriginal() {
        return valorTotalOriginal;
    }

    public void setValorTotalOriginal(BigDecimal valorTotalOriginal) {
        this.valorTotalOriginal = valorTotalOriginal;
    }

    public TipoProcesso getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcesso tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public boolean hasValorTotal(ItemControleProcessoMovimento item) {
        return item.getValorTotal().compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getValorTotalCalculado(BigDecimal quantidade, BigDecimal valorUnitario) {
        return quantidade.multiply(valorUnitario).setScale(2, RoundingMode.HALF_EVEN);
    }

    public Long getIdMovimento() {
        return idMovimento;
    }

    public void setIdMovimento(Long idMovimento) {
        this.idMovimento = idMovimento;
    }
}
