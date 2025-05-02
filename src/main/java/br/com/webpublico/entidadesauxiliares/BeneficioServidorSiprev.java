package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 05/11/14
 * Time: 18:01
 * To change this template use File | Settings | File Templates.
 */
public class BeneficioServidorSiprev {
    private Date dtInicioBeneficio;
    private Date dtFimBeneficio;
    private BigDecimal vlAtualBeneficio;
    private String cargo;
    private BigDecimal dependenteVinculoFPID;
    private BigDecimal idCargo;
    private BigDecimal idContratoFP;
    private BigDecimal idServidor;

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Date getDtInicioBeneficio() {
        return dtInicioBeneficio;
    }

    public void setDtInicioBeneficio(Date dtInicioBeneficio) {
        this.dtInicioBeneficio = dtInicioBeneficio;
    }

    public Date getDtFimBeneficio() {
        return dtFimBeneficio;
    }

    public void setDtFimBeneficio(Date dtFimBeneficio) {
        this.dtFimBeneficio = dtFimBeneficio;
    }

    public BigDecimal getVlAtualBeneficio() {
        return vlAtualBeneficio;
    }

    public void setVlAtualBeneficio(BigDecimal vlAtualBeneficio) {
        this.vlAtualBeneficio = vlAtualBeneficio;
    }

    public BigDecimal getDependenteVinculoFPID() {
        return dependenteVinculoFPID;
    }

    public void setDependenteVinculoFPID(BigDecimal dependenteVinculoFPID) {
        this.dependenteVinculoFPID = dependenteVinculoFPID;
    }

    public BigDecimal getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(BigDecimal idCargo) {
        this.idCargo = idCargo;
    }

    public BigDecimal getIdContratoFP() {
        return idContratoFP;
    }

    public void setIdContratoFP(BigDecimal idContratoFP) {
        this.idContratoFP = idContratoFP;
    }

    public BigDecimal getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(BigDecimal idServidor) {
        this.idServidor = idServidor;
    }
}

