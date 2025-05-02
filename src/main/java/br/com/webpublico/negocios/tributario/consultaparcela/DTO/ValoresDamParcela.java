package br.com.webpublico.negocios.tributario.consultaparcela.DTO;

import java.math.BigDecimal;

public class ValoresDamParcela {
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcaoMonetaria;
    private BigDecimal honorarios;

    public ValoresDamParcela(Object[] obj) {
        this.juros = (BigDecimal) obj[0];
        this.multa = (BigDecimal) obj[1];
        this.correcaoMonetaria = (BigDecimal) obj[2];
        this.honorarios = (BigDecimal) obj[3];
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecaoMonetaria() {
        return correcaoMonetaria;
    }

    public void setCorrecaoMonetaria(BigDecimal correcaoMonetaria) {
        this.correcaoMonetaria = correcaoMonetaria;
    }

    public BigDecimal getHonorarios() {
        return honorarios;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }
}




