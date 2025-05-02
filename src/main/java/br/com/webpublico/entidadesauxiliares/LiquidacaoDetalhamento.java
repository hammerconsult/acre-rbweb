package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 17/04/2015.
 */
public class LiquidacaoDetalhamento {
    private String codigo;
    private String descricao;
    private BigDecimal valor;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
}
