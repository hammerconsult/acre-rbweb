package br.com.webpublico.nfse.domain.dtos;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class TotalizadorRelatorioReceitaDeclaradaISSQNDTO {
    private String cadastroEconomico;
    private List<Long> idsCalculos;
    private BigDecimal iss;
    private BigDecimal valorPago;

    public TotalizadorRelatorioReceitaDeclaradaISSQNDTO() {
        idsCalculos = Lists.newArrayList();
    }

    public String getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(String cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<Long> getIdsCalculos() {
        return idsCalculos;
    }

    public void setIdsCalculos(List<Long> idsCalculos) {
        this.idsCalculos = idsCalculos;
    }

    public BigDecimal getIss() {
        return iss;
    }

    public void setIss(BigDecimal iss) {
        this.iss = iss;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalizadorRelatorioReceitaDeclaradaISSQNDTO that = (TotalizadorRelatorioReceitaDeclaradaISSQNDTO) o;
        return Objects.equals(cadastroEconomico, that.cadastroEconomico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cadastroEconomico);
    }
}
