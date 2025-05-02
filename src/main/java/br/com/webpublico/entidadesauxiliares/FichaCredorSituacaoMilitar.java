package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

public class FichaCredorSituacaoMilitar {

    private String tipo;
    private String certificadoMilitar;
    private String serie;
    private String categoria;
    private Date dataEmissao;
    private String orgao;

    public FichaCredorSituacaoMilitar() {
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCertificadoMilitar() {
        return certificadoMilitar;
    }

    public void setCertificadoMilitar(String certificadoMilitar) {
        this.certificadoMilitar = certificadoMilitar;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }
}
