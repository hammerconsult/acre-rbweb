package br.com.webpublico.negocios.tributario.consultaparcela.DTO;

import java.math.BigDecimal;

public class ValoresSubvencionadosParcela {
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcaoMonetaria;
    private BigDecimal honorarios;
    private BigDecimal total;
    private BigDecimal imposto;
    private BigDecimal taxa;

    public ValoresSubvencionadosParcela(Object[] obj) {
        this.juros = (BigDecimal) obj[0];
        this.multa = (BigDecimal) obj[1];
        this.correcaoMonetaria = (BigDecimal) obj[2];
        this.honorarios = (BigDecimal) obj[3];
        this.total = (BigDecimal) obj[4];
        this.imposto = (BigDecimal) obj[5];
        this.taxa = (BigDecimal) obj[6];
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getImposto() {
        return imposto;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }
}
