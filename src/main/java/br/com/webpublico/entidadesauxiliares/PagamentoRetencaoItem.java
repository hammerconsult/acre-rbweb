package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 26/11/2014.
 */
public class PagamentoRetencaoItem {
    private String descricao;
    private BigDecimal valorRetencao;

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
