package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 15/12/2014.
 */
public class DespesaPorCredorTransparencia {
    private String unidade;
    private BigDecimal empenhado;
    private BigDecimal pago;
    private BigDecimal liquidado;

    public DespesaPorCredorTransparencia() {
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getEmpenhado() {
        return empenhado;
    }

    public void setEmpenhado(BigDecimal empenhado) {
        this.empenhado = empenhado;
    }

    public BigDecimal getPago() {
        return pago;
    }

    public void setPago(BigDecimal pago) {
        this.pago = pago;
    }

    public BigDecimal getLiquidado() {
        return liquidado;
    }

    public void setLiquidado(BigDecimal liquidado) {
        this.liquidado = liquidado;
    }
}
