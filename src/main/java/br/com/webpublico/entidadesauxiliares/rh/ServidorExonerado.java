package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Alex
 * @since 01/02/2017 17:46
 */
public class ServidorExonerado {

    private Date dataExoneracao;
    private String matricula;
    private String numeroContrato;
    private String servidor;
    private String vinculo;
    private String pisPasep;
    private String cargo;
    private String lotacao;
    private String orgao;
    private String atoLegal;
    private Date emissaoAtoLegal;
    private BigDecimal vencimentoBase;

    public ServidorExonerado() {
    }


    public String getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(String atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getEmissaoAtoLegal() {
        return emissaoAtoLegal;
    }

    public void setEmissaoAtoLegal(Date emissaoAtoLegal) {
        this.emissaoAtoLegal = emissaoAtoLegal;
    }

    public BigDecimal getVencimentoBase() {
        return vencimentoBase;
    }

    public void setVencimentoBase(BigDecimal vencimentoBase) {
        this.vencimentoBase = vencimentoBase;
    }

    public Date getDataExoneracao() {
        return dataExoneracao;
    }

    public void setDataExoneracao(Date dataExoneracao) {
        this.dataExoneracao = dataExoneracao;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public String getVinculo() {
        return vinculo;
    }

    public void setVinculo(String vinculo) {
        this.vinculo = vinculo;
    }

    public String getPisPasep() {
        return pisPasep;
    }

    public void setPisPasep(String pisPasep) {
        this.pisPasep = pisPasep;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getLotacao() {
        return lotacao;
    }

    public void setLotacao(String lotacao) {
        this.lotacao = lotacao;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }
}
