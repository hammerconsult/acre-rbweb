package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by zaca on 20/10/16.
 */
public class DemonstrativoExecucaoOrcamentariaDespesaFuncaoVO {
    private String descricao;
    private BigDecimal saldoTesouro;
    private BigDecimal empenhadoTesouro;
    private BigDecimal diferencaTesouro;
    private BigDecimal saldoOutrasFontes;
    private BigDecimal empenhadoOutrasFontes;
    private BigDecimal diferencaOutrasFontes;
    private BigDecimal totalSaldoOrcado;
    private BigDecimal totalEmpenhado;
    private BigDecimal totalDiferenca;

    public DemonstrativoExecucaoOrcamentariaDespesaFuncaoVO() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getSaldoTesouro() {
        return saldoTesouro;
    }

    public void setSaldoTesouro(BigDecimal saldoTesouro) {
        this.saldoTesouro = saldoTesouro;
    }

    public BigDecimal getEmpenhadoTesouro() {
        return empenhadoTesouro;
    }

    public void setEmpenhadoTesouro(BigDecimal empenhadoTesouro) {
        this.empenhadoTesouro = empenhadoTesouro;
    }

    public BigDecimal getDiferencaTesouro() {
        return diferencaTesouro;
    }

    public void setDiferencaTesouro(BigDecimal diferencaTesouro) {
        this.diferencaTesouro = diferencaTesouro;
    }

    public BigDecimal getSaldoOutrasFontes() {
        return saldoOutrasFontes;
    }

    public void setSaldoOutrasFontes(BigDecimal saldoOutrasFontes) {
        this.saldoOutrasFontes = saldoOutrasFontes;
    }

    public BigDecimal getEmpenhadoOutrasFontes() {
        return empenhadoOutrasFontes;
    }

    public void setEmpenhadoOutrasFontes(BigDecimal empenhadoOutrasFontes) {
        this.empenhadoOutrasFontes = empenhadoOutrasFontes;
    }

    public BigDecimal getDiferencaOutrasFontes() {
        return diferencaOutrasFontes;
    }

    public void setDiferencaOutrasFontes(BigDecimal diferencaOutrasFontes) {
        this.diferencaOutrasFontes = diferencaOutrasFontes;
    }

    public BigDecimal getTotalSaldoOrcado() {
        return totalSaldoOrcado;
    }

    public void setTotalSaldoOrcado(BigDecimal totalSaldoOrcado) {
        this.totalSaldoOrcado = totalSaldoOrcado;
    }

    public BigDecimal getTotalEmpenhado() {
        return totalEmpenhado;
    }

    public void setTotalEmpenhado(BigDecimal totalEmpenhado) {
        this.totalEmpenhado = totalEmpenhado;
    }

    public BigDecimal getTotalDiferenca() {
        return totalDiferenca;
    }

    public void setTotalDiferenca(BigDecimal totalDiferenca) {
        this.totalDiferenca = totalDiferenca;
    }
}
