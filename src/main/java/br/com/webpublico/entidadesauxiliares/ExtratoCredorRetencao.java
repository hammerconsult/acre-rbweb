package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 02/12/2014.
 */
public class ExtratoCredorRetencao {
    private String numero;
    private String numeroReceitaExtra;
    private String descricao;
    private BigDecimal valor;
    private Boolean estorno;

    public ExtratoCredorRetencao() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNumeroReceitaExtra() {
        return numeroReceitaExtra;
    }

    public void setNumeroReceitaExtra(String numeroReceitaExtra) {
        this.numeroReceitaExtra = numeroReceitaExtra;
    }

    public Boolean getEstorno() {
        return estorno;
    }

    public void setEstorno(Boolean estorno) {
        this.estorno = estorno;
    }
}
