package br.com.webpublico.entidadesauxiliares.rh.comparativofolhasdepagamento;

import br.com.webpublico.entidades.VinculoFP;

import java.math.BigDecimal;

public class ItemComparativoFolhasDePagamento {
    private VinculoFP vinculoFP;
    private BigDecimal vantagem1;
    private BigDecimal vantagem2;
    private BigDecimal vantagemDiferenca;
    private BigDecimal desconto1;
    private BigDecimal desconto2;
    private BigDecimal descontoDiferenca;
    private BigDecimal liquido1;
    private BigDecimal liquido2;
    private BigDecimal liquidoDiferenca;

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public BigDecimal getVantagem1() {
        return vantagem1;
    }

    public void setVantagem1(BigDecimal vantagem1) {
        this.vantagem1 = vantagem1;
    }

    public BigDecimal getVantagem2() {
        return vantagem2;
    }

    public void setVantagem2(BigDecimal vantagem2) {
        this.vantagem2 = vantagem2;
    }

    public BigDecimal getVantagemDiferenca() {
        return vantagemDiferenca;
    }

    public void setVantagemDiferenca(BigDecimal vantagemDiferenca) {
        this.vantagemDiferenca = vantagemDiferenca;
    }

    public BigDecimal getDesconto1() {
        return desconto1;
    }

    public void setDesconto1(BigDecimal desconto1) {
        this.desconto1 = desconto1;
    }

    public BigDecimal getDesconto2() {
        return desconto2;
    }

    public void setDesconto2(BigDecimal desconto2) {
        this.desconto2 = desconto2;
    }

    public BigDecimal getDescontoDiferenca() {
        return descontoDiferenca;
    }

    public void setDescontoDiferenca(BigDecimal descontoDiferenca) {
        this.descontoDiferenca = descontoDiferenca;
    }

    public BigDecimal getLiquido1() {
        return liquido1;
    }

    public void setLiquido1(BigDecimal liquido1) {
        this.liquido1 = liquido1;
    }

    public BigDecimal getLiquido2() {
        return liquido2;
    }

    public void setLiquido2(BigDecimal liquido2) {
        this.liquido2 = liquido2;
    }

    public BigDecimal getLiquidoDiferenca() {
        return liquidoDiferenca;
    }

    public void setLiquidoDiferenca(BigDecimal liquidoDiferenca) {
        this.liquidoDiferenca = liquidoDiferenca;
    }
}
