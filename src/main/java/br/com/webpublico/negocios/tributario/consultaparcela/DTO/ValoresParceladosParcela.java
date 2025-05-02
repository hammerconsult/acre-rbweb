package br.com.webpublico.negocios.tributario.consultaparcela.DTO;

import java.math.BigDecimal;
import java.util.Date;

public class ValoresParceladosParcela {
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcaoMonetaria;
    private BigDecimal honorarios;
    private Date dataParcelamento;
    private Date dataCancelamento;

    public ValoresParceladosParcela(Object[] obj) {
        this.imposto = (BigDecimal) obj[0];
        this.taxa = (BigDecimal) obj[1];
        this.juros = (BigDecimal) obj[2];
        this.multa = (BigDecimal) obj[3];
        this.correcaoMonetaria = (BigDecimal) obj[4];
        this.honorarios = (BigDecimal) obj[5];
        this.dataParcelamento = (Date) obj[6];
        this.dataCancelamento = (Date) obj[7];
    }

    public BigDecimal getJuros() {
        return juros != null ? juros : BigDecimal.ZERO;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa != null ? multa : BigDecimal.ZERO;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecaoMonetaria() {
        return correcaoMonetaria != null ? correcaoMonetaria : BigDecimal.ZERO;
    }

    public void setCorrecaoMonetaria(BigDecimal correcaoMonetaria) {
        this.correcaoMonetaria = correcaoMonetaria;
    }

    public BigDecimal getHonorarios() {
        return honorarios != null ? honorarios : BigDecimal.ZERO;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public BigDecimal getImposto() {
        return imposto != null ? imposto : BigDecimal.ZERO;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa != null ? taxa : BigDecimal.ZERO;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public Date getDataParcelamento() {
        return dataParcelamento;
    }

    public void setDataParcelamento(Date dataParcelamento) {
        this.dataParcelamento = dataParcelamento;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public BigDecimal getTotal() {
        return getImposto().add(getTaxa().add(getJuros().add(getMulta().add(getCorrecaoMonetaria().add(getHonorarios())))));
    }
}
