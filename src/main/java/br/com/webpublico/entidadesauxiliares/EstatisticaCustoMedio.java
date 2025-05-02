package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Criado por Mateus
 * Data: 26/05/2017.
 */
public class EstatisticaCustoMedio {

    private BigDecimal custoMedio;
    private String periodo;

    public BigDecimal getCustoMedio() {
        return custoMedio;
    }

    public void setCustoMedio(BigDecimal custoMedio) {
        this.custoMedio = custoMedio;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public EstatisticaCustoMedio() {

    }
}
