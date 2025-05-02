package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 02/06/2017.
 */
public class ServidorInfoCargoComissao {

    private Integer item;
    private Date inicioVigencia;
    private Date fimVigencia;
    private String cargo;
    private String opcaoSalarial;
    private BigDecimal percentual;
    private Date dataRegistro;
    private String enquadramento;
    private List<ServidorInfoCargoComissao> enquadramentoCCs;

    public ServidorInfoCargoComissao() {
        enquadramentoCCs = new ArrayList<>();
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getOpcaoSalarial() {
        return opcaoSalarial;
    }

    public void setOpcaoSalarial(String opcaoSalarial) {
        this.opcaoSalarial = opcaoSalarial;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getEnquadramento() {
        return enquadramento;
    }

    public void setEnquadramento(String enquadramento) {
        this.enquadramento = enquadramento;
    }

    public List<ServidorInfoCargoComissao> getEnquadramentoCCs() {
        return enquadramentoCCs;
    }

    public void setEnquadramentoCCs(List<ServidorInfoCargoComissao> enquadramentoCCs) {
        this.enquadramentoCCs = enquadramentoCCs;
    }
}
