package br.com.webpublico.nfse.domain.dtos;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class TotalizadorRelatorioApuracaoISSQNDTO {
    private String servico;
    private BigDecimal aliquotaServico;
    private BigDecimal iss;
    private BigDecimal baseCalculo;
    private BigDecimal deducoes;
    private BigDecimal descontosCondicionados;
    private BigDecimal descontosIncondicionados;
    private BigDecimal valorServico;

    public TotalizadorRelatorioApuracaoISSQNDTO() {
        aliquotaServico = BigDecimal.ZERO;
        iss = BigDecimal.ZERO;
        baseCalculo = BigDecimal.ZERO;
        deducoes = BigDecimal.ZERO;
        descontosCondicionados = BigDecimal.ZERO;
        descontosIncondicionados = BigDecimal.ZERO;
        valorServico = BigDecimal.ZERO;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public BigDecimal getAliquotaServico() {
        return aliquotaServico;
    }

    public void setAliquotaServico(BigDecimal aliquotaServico) {
        this.aliquotaServico = aliquotaServico;
    }

    public BigDecimal getIss() {
        return iss;
    }

    public void setIss(BigDecimal iss) {
        this.iss = iss;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getDeducoes() {
        return deducoes;
    }

    public void setDeducoes(BigDecimal deducoes) {
        this.deducoes = deducoes;
    }

    public BigDecimal getDescontosCondicionados() {
        return descontosCondicionados;
    }

    public void setDescontosCondicionados(BigDecimal descontosCondicionados) {
        this.descontosCondicionados = descontosCondicionados;
    }

    public BigDecimal getDescontosIncondicionados() {
        return descontosIncondicionados;
    }

    public void setDescontosIncondicionados(BigDecimal descontosIncondicionados) {
        this.descontosIncondicionados = descontosIncondicionados;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalizadorRelatorioApuracaoISSQNDTO that = (TotalizadorRelatorioApuracaoISSQNDTO) o;
        return Objects.equals(servico, that.servico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(servico);
    }
}
