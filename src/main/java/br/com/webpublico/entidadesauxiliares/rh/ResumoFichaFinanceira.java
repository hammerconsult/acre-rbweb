package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;

/**
 * @Author peixe on 30/03/2017  09:45.
 */
public class ResumoFichaFinanceira {

    private BigDecimal idVinculo;
    private BigDecimal vencimentoBase;
    private BigDecimal valorBruto;
    private BigDecimal valorDesconto;
    private BigDecimal outrasVerbas;
    private BigDecimal totalLiquido;
    private BigDecimal margem;

    public ResumoFichaFinanceira(BigDecimal idVinculo, BigDecimal vencimentoBase, BigDecimal valorBruto, BigDecimal valorDesconto, BigDecimal outrasVerbas, BigDecimal totalLiquido, BigDecimal margem) {
        this.idVinculo = idVinculo;
        this.vencimentoBase = vencimentoBase;
        this.valorBruto = valorBruto;
        this.valorDesconto = valorDesconto;
        this.outrasVerbas = outrasVerbas;
        this.totalLiquido = totalLiquido;
        this.margem = margem;
    }

    public BigDecimal getIdVinculo() {
        return idVinculo;
    }

    public void setIdVinculo(BigDecimal idVinculo) {
        this.idVinculo = idVinculo;
    }

    public BigDecimal getVencimentoBase() {
        return vencimentoBase;
    }

    public void setVencimentoBase(BigDecimal vencimentoBase) {
        this.vencimentoBase = vencimentoBase;
    }

    public BigDecimal getValorBruto() {
        return valorBruto;
    }

    public void setValorBruto(BigDecimal valorBruto) {
        this.valorBruto = valorBruto;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public BigDecimal getOutrasVerbas() {
        return outrasVerbas;
    }

    public void setOutrasVerbas(BigDecimal outrasVerbas) {
        this.outrasVerbas = outrasVerbas;
    }

    public BigDecimal getTotalLiquido() {
        return totalLiquido;
    }

    public void setTotalLiquido(BigDecimal totalLiquido) {
        this.totalLiquido = totalLiquido;
    }

    public BigDecimal getMargem() {
        return margem;
    }

    public void setMargem(BigDecimal margem) {
        this.margem = margem;
    }
}
