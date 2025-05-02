package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 16/12/2014.
 */
public class ComprasResumoOrgaoTransparencia {
    private BigDecimal totalLiquidado;
    private BigDecimal totalLancamentos;
    private BigDecimal totalOrgao;
    private BigDecimal totalUnidade;

    public ComprasResumoOrgaoTransparencia() {
    }

    public BigDecimal getTotalLiquidado() {
        return totalLiquidado;
    }

    public void setTotalLiquidado(BigDecimal totalLiquidado) {
        this.totalLiquidado = totalLiquidado;
    }

    public BigDecimal getTotalLancamentos() {
        return totalLancamentos;
    }

    public void setTotalLancamentos(BigDecimal totalLancamentos) {
        this.totalLancamentos = totalLancamentos;
    }

    public BigDecimal getTotalOrgao() {
        return totalOrgao;
    }

    public void setTotalOrgao(BigDecimal totalOrgao) {
        this.totalOrgao = totalOrgao;
    }

    public BigDecimal getTotalUnidade() {
        return totalUnidade;
    }

    public void setTotalUnidade(BigDecimal totalUnidade) {
        this.totalUnidade = totalUnidade;
    }
}
