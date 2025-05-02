package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Criado por Mateus
 * Data: 20/02/2017.
 */
public class DemonstrativoDespesaNaturezaDetalhamentoLiquidacao {
    private BigDecimal valorLiquidado;
    private Long idLiquidacao;

    public BigDecimal getValorLiquidado() {
        return valorLiquidado;
    }

    public void setValorLiquidado(BigDecimal valorLiquidado) {
        this.valorLiquidado = valorLiquidado;
    }

    public Long getIdLiquidacao() {
        return idLiquidacao;
    }

    public void setIdLiquidacao(Long idLiquidacao) {
        this.idLiquidacao = idLiquidacao;
    }
}
