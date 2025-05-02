package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fox_m on 30/03/2016.
 */
public class TermoRescisaoVerba {
    private List<TermoRescisaoVerbaDescontoEVantagem> descontos;
    private List<TermoRescisaoVerbaDescontoEVantagem> vantagens;
    private BigDecimal dependentesIR;
    private BigDecimal dependentesSSF;
    private BigDecimal baseFGTS;
    private BigDecimal baseIRRF;
    private BigDecimal basePrev;

    public TermoRescisaoVerba() {
        descontos = new ArrayList<>();
        vantagens = new ArrayList<>();
        dependentesIR = BigDecimal.ZERO;
        dependentesSSF = BigDecimal.ZERO;
        baseFGTS = BigDecimal.ZERO;
        baseIRRF = BigDecimal.ZERO;
        basePrev = BigDecimal.ZERO;
    }

    public List<TermoRescisaoVerbaDescontoEVantagem> getDescontos() {
        return descontos;
    }

    public void setDescontos(List<TermoRescisaoVerbaDescontoEVantagem> descontos) {
        this.descontos = descontos;
    }

    public List<TermoRescisaoVerbaDescontoEVantagem> getVantagens() {
        return vantagens;
    }

    public void setVantagens(List<TermoRescisaoVerbaDescontoEVantagem> vantagens) {
        this.vantagens = vantagens;
    }

    public BigDecimal getDependentesIR() {
        return dependentesIR;
    }

    public void setDependentesIR(BigDecimal dependentesIR) {
        this.dependentesIR = dependentesIR;
    }

    public BigDecimal getDependentesSSF() {
        return dependentesSSF;
    }

    public void setDependentesSSF(BigDecimal dependentesSSF) {
        this.dependentesSSF = dependentesSSF;
    }

    public BigDecimal getBaseFGTS() {
        return baseFGTS;
    }

    public void setBaseFGTS(BigDecimal baseFGTS) {
        this.baseFGTS = baseFGTS;
    }

    public BigDecimal getBaseIRRF() {
        return baseIRRF;
    }

    public void setBaseIRRF(BigDecimal baseIRRF) {
        this.baseIRRF = baseIRRF;
    }

    public BigDecimal getBasePrev() {
        return basePrev;
    }

    public void setBasePrev(BigDecimal basePrev) {
        this.basePrev = basePrev;
    }
}
