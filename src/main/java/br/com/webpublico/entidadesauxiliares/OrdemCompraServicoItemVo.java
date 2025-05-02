package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoMascaraUnidadeMedida;

import java.math.BigDecimal;

public class OrdemCompraServicoItemVo {

    private Integer numero;
    private String objetoCompra;
    private String descricaoComplementar;
    private String unidadeMedida;
    private TipoMascaraUnidadeMedida mascaraQuantidade;
    private TipoMascaraUnidadeMedida mascaraValor;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
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

    public String getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(String objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public TipoMascaraUnidadeMedida getMascaraQuantidade() {
        return mascaraQuantidade;
    }

    public void setMascaraQuantidade(TipoMascaraUnidadeMedida mascaraQuantidade) {
        this.mascaraQuantidade = mascaraQuantidade;
    }

    public TipoMascaraUnidadeMedida getMascaraValor() {
        return mascaraValor;
    }

    public void setMascaraValor(TipoMascaraUnidadeMedida mascaraValor) {
        this.mascaraValor = mascaraValor;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }
}
