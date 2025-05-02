package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 31/03/15
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
public class ValidadorValoresOBN600 {
    private String idBordero;
    private String bordero;
    private BigDecimal pagamento;
    private BigDecimal despesaExtra;
    private BigDecimal totalMovimentos;
    private BigDecimal valorItemBordero;
    private BigDecimal totalBordero;
    private Boolean possuiDiferenca;

    public ValidadorValoresOBN600() {
    }

    public String getBordero() {
        return bordero;
    }

    public void setBordero(String bordero) {
        this.bordero = bordero;
    }

    public BigDecimal getPagamento() {
        return pagamento;
    }

    public void setPagamento(BigDecimal pagamento) {
        this.pagamento = pagamento;
    }

    public BigDecimal getDespesaExtra() {
        return despesaExtra;
    }

    public void setDespesaExtra(BigDecimal despesaExtra) {
        this.despesaExtra = despesaExtra;
    }

    public BigDecimal getTotalMovimentos() {
        return totalMovimentos;
    }

    public void setTotalMovimentos(BigDecimal totalMovimentos) {
        this.totalMovimentos = totalMovimentos;
    }

    public BigDecimal getTotalBordero() {
        return totalBordero;
    }

    public void setTotalBordero(BigDecimal totalBordero) {
        this.totalBordero = totalBordero;
    }

    public Boolean getPossuiDiferenca() {
        return possuiDiferenca;
    }

    public void setPossuiDiferenca(Boolean possuiDiferenca) {
        this.possuiDiferenca = possuiDiferenca;
    }

    public BigDecimal getValorItemBordero() {
        return valorItemBordero;
    }

    public void setValorItemBordero(BigDecimal valorItemBordero) {
        this.valorItemBordero = valorItemBordero;
    }

    public String getIdBordero() {
        return idBordero;
    }

    public void setIdBordero(String idBordero) {
        this.idBordero = idBordero;
    }
}
