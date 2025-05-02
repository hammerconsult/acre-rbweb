package br.com.webpublico.entidadesauxiliares.rh;


import java.util.Date;

public class CedenciaPortal {
    // Pode ser id da cedência ou do afastamento com código 14 ou 15
    private Long idExterno;
    private Date inicio;
    private Date termino;
    private String esfera;
    private String orgaoReceptor;
    private String finalidade;
    private String tipoCedencia;

    public Long getIdExterno() {
        return idExterno;
    }

    public void setIdExterno(Long idExterno) {
        this.idExterno = idExterno;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getTermino() {
        return termino;
    }

    public void setTermino(Date termino) {
        this.termino = termino;
    }

    public String getEsfera() {
        return esfera;
    }

    public void setEsfera(String esfera) {
        this.esfera = esfera;
    }

    public String getOrgaoReceptor() {
        return orgaoReceptor;
    }

    public void setOrgaoReceptor(String orgaoReceptor) {
        this.orgaoReceptor = orgaoReceptor;
    }

    public String getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(String finalidade) {
        this.finalidade = finalidade;
    }

    public String getTipoCedencia() {
        return tipoCedencia;
    }

    public void setTipoCedencia(String tipoCedencia) {
        this.tipoCedencia = tipoCedencia;
    }
}
