package br.com.webpublico.entidadesauxiliares.rh.comparativofolhasdepagamento;

import com.beust.jcommander.internal.Lists;

import java.math.BigDecimal;
import java.util.List;

public class ComparativoFolhasDePagamento {
    private BigDecimal totalVantagem1;
    private BigDecimal totalVantagem2;
    private BigDecimal totalDesconto1;
    private BigDecimal totalDesconto2;
    private BigDecimal totalLiquido1;
    private BigDecimal totalLiquido2;
    private BigDecimal totalVantagemDiferenca;
    private BigDecimal totalDescontoDiferenca;
    private BigDecimal totalLiquidoDiferenca;
    private List<ItemComparativoFolhasDePagamento> itens;

    public ComparativoFolhasDePagamento() {
        totalVantagem1 = BigDecimal.ZERO;
        totalVantagem2 = BigDecimal.ZERO;
        totalDesconto1 = BigDecimal.ZERO;
        totalDesconto2 = BigDecimal.ZERO;
        totalLiquido1 = BigDecimal.ZERO;
        totalLiquido2 = BigDecimal.ZERO;
        totalVantagemDiferenca = BigDecimal.ZERO;
        totalDescontoDiferenca = BigDecimal.ZERO;
        totalLiquidoDiferenca = BigDecimal.ZERO;
        itens = Lists.newArrayList();
    }

    public BigDecimal getTotalVantagem1() {
        return totalVantagem1;
    }

    public void setTotalVantagem1(BigDecimal totalVantagem1) {
        this.totalVantagem1 = totalVantagem1;
    }

    public BigDecimal getTotalVantagem2() {
        return totalVantagem2;
    }

    public void setTotalVantagem2(BigDecimal totalVantagem2) {
        this.totalVantagem2 = totalVantagem2;
    }

    public BigDecimal getTotalDesconto1() {
        return totalDesconto1;
    }

    public void setTotalDesconto1(BigDecimal totalDesconto1) {
        this.totalDesconto1 = totalDesconto1;
    }

    public BigDecimal getTotalDesconto2() {
        return totalDesconto2;
    }

    public void setTotalDesconto2(BigDecimal totalDesconto2) {
        this.totalDesconto2 = totalDesconto2;
    }

    public BigDecimal getTotalLiquido1() {
        return totalLiquido1;
    }

    public void setTotalLiquido1(BigDecimal totalLiquido1) {
        this.totalLiquido1 = totalLiquido1;
    }

    public BigDecimal getTotalLiquido2() {
        return totalLiquido2;
    }

    public void setTotalLiquido2(BigDecimal totalLiquido2) {
        this.totalLiquido2 = totalLiquido2;
    }

    public BigDecimal getTotalVantagemDiferenca() {
        return totalVantagemDiferenca;
    }

    public void setTotalVantagemDiferenca(BigDecimal totalVantagemDiferenca) {
        this.totalVantagemDiferenca = totalVantagemDiferenca;
    }

    public BigDecimal getTotalDescontoDiferenca() {
        return totalDescontoDiferenca;
    }

    public void setTotalDescontoDiferenca(BigDecimal totalDescontoDiferenca) {
        this.totalDescontoDiferenca = totalDescontoDiferenca;
    }

    public BigDecimal getTotalLiquidoDiferenca() {
        return totalLiquidoDiferenca;
    }

    public void setTotalLiquidoDiferenca(BigDecimal totalLiquidoDiferenca) {
        this.totalLiquidoDiferenca = totalLiquidoDiferenca;
    }

    public List<ItemComparativoFolhasDePagamento> getItens() {
        return itens;
    }

    public void setItens(List<ItemComparativoFolhasDePagamento> itens) {
        this.itens = itens;
    }
}
