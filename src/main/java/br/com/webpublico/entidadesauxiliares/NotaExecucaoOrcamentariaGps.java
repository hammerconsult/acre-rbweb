package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

public class NotaExecucaoOrcamentariaGps {
    private String tipoGuia;
    private String codigoTributo;
    private Date periodoComp;
    private String digitoTributo;
    private BigDecimal valor;

    public NotaExecucaoOrcamentariaGps() {
    }

    public String getTipoGuia() {
        return tipoGuia;
    }

    public void setTipoGuia(String tipoGuia) {
        this.tipoGuia = tipoGuia;
    }

    public String getCodigoTributo() {
        return codigoTributo;
    }

    public void setCodigoTributo(String codigoTributo) {
        this.codigoTributo = codigoTributo;
    }

    public Date getPeriodoComp() {
        return periodoComp;
    }

    public void setPeriodoComp(Date periodoComp) {
        this.periodoComp = periodoComp;
    }

    public String getDigitoTributo() {
        return digitoTributo;
    }

    public void setDigitoTributo(String digitoTributo) {
        this.digitoTributo = digitoTributo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
