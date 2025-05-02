package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class ConsultaDespesaFuncaoSubfuncao {
    private String funcao;
    private String subfuncao;
    private BigDecimal empenhado;
    private BigDecimal liquidado;
    private BigDecimal pago;

    public ConsultaDespesaFuncaoSubfuncao() {
        empenhado = BigDecimal.ZERO;
        liquidado = BigDecimal.ZERO;
        pago = BigDecimal.ZERO;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getSubfuncao() {
        return subfuncao;
    }

    public void setSubfuncao(String subfuncao) {
        this.subfuncao = subfuncao;
    }

    public BigDecimal getEmpenhado() {
        return empenhado;
    }

    public void setEmpenhado(BigDecimal empenhado) {
        this.empenhado = empenhado;
    }

    public BigDecimal getLiquidado() {
        return liquidado;
    }

    public void setLiquidado(BigDecimal liquidado) {
        this.liquidado = liquidado;
    }

    public BigDecimal getPago() {
        return pago;
    }

    public void setPago(BigDecimal pago) {
        this.pago = pago;
    }

    public BigDecimal getALiquidar() {
        return empenhado.subtract(liquidado);
    }

    public BigDecimal getLiquidadaAPagar() {
        return liquidado.subtract(pago);
    }

    public boolean hasValor() {
        return empenhado.compareTo(BigDecimal.ZERO) != 0 ||
            liquidado.compareTo(BigDecimal.ZERO) != 0 ||
            pago.compareTo(BigDecimal.ZERO) != 0;
    }
}
