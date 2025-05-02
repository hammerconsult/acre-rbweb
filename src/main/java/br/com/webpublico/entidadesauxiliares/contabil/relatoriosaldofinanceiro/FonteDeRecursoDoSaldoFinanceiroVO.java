package br.com.webpublico.entidadesauxiliares.contabil.relatoriosaldofinanceiro;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by carlos on 15/03/17.
 */
public class FonteDeRecursoDoSaldoFinanceiroVO {
    private String fonte;
    private BigDecimal soma;
    private Date dataSaldo;
    private String vwOrg;

    public FonteDeRecursoDoSaldoFinanceiroVO() {
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public BigDecimal getSoma() {
        return soma;
    }

    public void setSoma(BigDecimal soma) {
        this.soma = soma;
    }

    public Date getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(Date dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public String getVwOrg() {
        return vwOrg;
    }

    public void setVwOrg(String vwOrg) {
        this.vwOrg = vwOrg;
    }
}
