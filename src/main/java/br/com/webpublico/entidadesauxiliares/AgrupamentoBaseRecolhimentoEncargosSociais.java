package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Criado por Mateus
 * Data: 23/06/2017.
 */
public class AgrupamentoBaseRecolhimentoEncargosSociais {

    private String grupo;
    private String codigoRecurso;
    private String descricaoRecurso;
    private String matricula;
    private String servidor;
    private BigDecimal inss;
    private BigDecimal baseInss;
    private BigDecimal rpps;
    private BigDecimal baseRpps;
    private BigDecimal irrf;
    private BigDecimal baseIrrf;
    private BigDecimal salarioMaternidade;
    private BigDecimal baseSalarioMaternidade;
    private BigDecimal salarioFamilia;
    private BigDecimal baseSalarioFamilia;
    private BigDecimal fgts;
    private BigDecimal baseFgts;

    public AgrupamentoBaseRecolhimentoEncargosSociais() {
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getCodigoRecurso() {
        return codigoRecurso;
    }

    public void setCodigoRecurso(String codigoRecurso) {
        this.codigoRecurso = codigoRecurso;
    }

    public String getDescricaoRecurso() {
        return descricaoRecurso;
    }

    public void setDescricaoRecurso(String descricaoRecurso) {
        this.descricaoRecurso = descricaoRecurso;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public BigDecimal getInss() {
        return inss;
    }

    public void setInss(BigDecimal inss) {
        this.inss = inss;
    }

    public BigDecimal getBaseInss() {
        return baseInss;
    }

    public void setBaseInss(BigDecimal baseInss) {
        this.baseInss = baseInss;
    }

    public BigDecimal getRpps() {
        return rpps;
    }

    public void setRpps(BigDecimal rpps) {
        this.rpps = rpps;
    }

    public BigDecimal getBaseRpps() {
        return baseRpps;
    }

    public void setBaseRpps(BigDecimal baseRpps) {
        this.baseRpps = baseRpps;
    }

    public BigDecimal getIrrf() {
        return irrf;
    }

    public void setIrrf(BigDecimal irrf) {
        this.irrf = irrf;
    }

    public BigDecimal getBaseIrrf() {
        return baseIrrf;
    }

    public void setBaseIrrf(BigDecimal baseIrrf) {
        this.baseIrrf = baseIrrf;
    }

    public BigDecimal getSalarioMaternidade() {
        return salarioMaternidade;
    }

    public void setSalarioMaternidade(BigDecimal salarioMaternidade) {
        this.salarioMaternidade = salarioMaternidade;
    }

    public BigDecimal getBaseSalarioMaternidade() {
        return baseSalarioMaternidade;
    }

    public void setBaseSalarioMaternidade(BigDecimal baseSalarioMaternidade) {
        this.baseSalarioMaternidade = baseSalarioMaternidade;
    }

    public BigDecimal getSalarioFamilia() {
        return salarioFamilia;
    }

    public void setSalarioFamilia(BigDecimal salarioFamilia) {
        this.salarioFamilia = salarioFamilia;
    }

    public BigDecimal getBaseSalarioFamilia() {
        return baseSalarioFamilia;
    }

    public void setBaseSalarioFamilia(BigDecimal baseSalarioFamilia) {
        this.baseSalarioFamilia = baseSalarioFamilia;
    }

    public BigDecimal getFgts() {
        return fgts;
    }

    public void setFgts(BigDecimal fgts) {
        this.fgts = fgts;
    }

    public BigDecimal getBaseFgts() {
        return baseFgts;
    }

    public void setBaseFgts(BigDecimal baseFgts) {
        this.baseFgts = baseFgts;
    }
}
