package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 16/12/2014.
 */
public class ComprasElementoDespesaTransparencia {
    private String descricao;
    private String codigoSuperior;
    private String codigo;
    private BigDecimal valor;

    public ComprasElementoDespesaTransparencia() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigoSuperior() {
        return codigoSuperior;
    }

    public void setCodigoSuperior(String codigoSuperior) {
        this.codigoSuperior = codigoSuperior;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
