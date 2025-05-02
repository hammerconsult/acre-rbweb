package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;

public class ParametrosRelatorioConferenciaSefip {

    private Long idContratoFp;
    private String matricula;
    private String contrato;
    private String servidor;
    private String pisPasep;
    private BigDecimal base;
    private BigDecimal base13;
    private BigDecimal valorPatronal;
    private BigDecimal valorPatronal13;
    private BigDecimal inss;
    private BigDecimal inss13;
    private BigDecimal fgts;
    private BigDecimal fgts13;
    private BigDecimal salarioMaternidade;
    private BigDecimal salarioFamilia;
    private BigDecimal rat;
    private BigDecimal rat13;

    public ParametrosRelatorioConferenciaSefip() {
        this.base = BigDecimal.ZERO;
        this.valorPatronal = BigDecimal.ZERO;
        this.inss = BigDecimal.ZERO;
        this.fgts = BigDecimal.ZERO;
        this.salarioMaternidade = BigDecimal.ZERO;
        this.salarioFamilia = BigDecimal.ZERO;
        this.rat = BigDecimal.ZERO;
        this.rat13 = BigDecimal.ZERO;
        this.inss13 = BigDecimal.ZERO;
        this.base13 = BigDecimal.ZERO;
        this.valorPatronal13 = BigDecimal.ZERO;
        this.fgts13= BigDecimal.ZERO;
    }

    public Long getIdContratoFp() {
        return idContratoFp;
    }

    public void setIdContratoFp(Long idContratoFp) {
        this.idContratoFp = idContratoFp;
    }

    public BigDecimal getRat() {
        return rat;
    }

    public void setRat(BigDecimal rat) {
        this.rat = rat;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public String getPisPasep() {
        return pisPasep;
    }

    public void setPisPasep(String pisPasep) {
        this.pisPasep = pisPasep;
    }

    public BigDecimal getBase() {
        return base;
    }

    public void setBase(BigDecimal base) {
        this.base = base;
    }

    public BigDecimal getValorPatronal() {
        return valorPatronal;
    }

    public void setValorPatronal(BigDecimal valorPatronal) {
        this.valorPatronal = valorPatronal;
    }

    public BigDecimal getInss() {
        return inss;
    }

    public void setInss(BigDecimal inss) {
        this.inss = inss;
    }

    public BigDecimal getBase13() {
        return base13;
    }

    public void setBase13(BigDecimal base13) {
        this.base13 = base13;
    }

    public BigDecimal getValorPatronal13() {
        return valorPatronal13;
    }

    public void setValorPatronal13(BigDecimal valorPatronal13) {
        this.valorPatronal13 = valorPatronal13;
    }

    public BigDecimal getInss13() {
        return inss13;
    }

    public void setInss13(BigDecimal inss13) {
        this.inss13 = inss13;
    }

    public BigDecimal getFgts13() {
        return fgts13;
    }

    public void setFgts13(BigDecimal fgts13) {
        this.fgts13 = fgts13;
    }

    public BigDecimal getRat13() {
        return rat13;
    }

    public void setRat13(BigDecimal rat13) {
        this.rat13 = rat13;
    }

    public BigDecimal getFgts() {
        return fgts;
    }

    public void setFgts(BigDecimal fgts) {
        this.fgts = fgts;
    }

    public BigDecimal getSalarioMaternidade() {
        return salarioMaternidade;
    }

    public void setSalarioMaternidade(BigDecimal salarioMaternidade) {
        this.salarioMaternidade = salarioMaternidade;
    }

    public BigDecimal getSalarioFamilia() {
        return salarioFamilia;
    }

    public void setSalarioFamilia(BigDecimal salarioFamilia) {
        this.salarioFamilia = salarioFamilia;
    }

}
