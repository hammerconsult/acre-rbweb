package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Criado por Mateus
 * Data: 07/06/2016.
 */
public class PagamentoPorCredorRetencao {
    private String descricao;
    private BigDecimal valorRetencao;

    public PagamentoPorCredorRetencao() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorRetencao() {
        return valorRetencao;
    }

    public void setValorRetencao(BigDecimal valorRetencao) {
        this.valorRetencao = valorRetencao;
    }
}
