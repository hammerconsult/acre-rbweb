package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.UnidadeMedida;
import br.com.webpublico.enums.TipoControleLicitacao;

import java.math.BigDecimal;

public class AtaRegistroPrecoItensContrato {

    private Long idContrato;
    private String contrato;
    private Long numero;
    private String descricao;
    private String objetoCompra;
    private String descricaoComplementar;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private String mascaraQuantidade;
    private String mascaraValorUnitario;
    private UnidadeMedida unidadeMedida;
    private TipoControleLicitacao tipoControle;

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(String objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
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

    public String getMascaraQuantidade() {
        return mascaraQuantidade;
    }

    public void setMascaraQuantidade(String mascaraQuantidade) {
        this.mascaraQuantidade = mascaraQuantidade;
    }

    public String getMascaraValorUnitario() {
        return mascaraValorUnitario;
    }

    public void setMascaraValorUnitario(String mascaraValorUnitario) {
        this.mascaraValorUnitario = mascaraValorUnitario;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }
}
