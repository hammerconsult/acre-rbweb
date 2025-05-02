package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by carlos on 03/04/17.
 */
public class SituacaoMilitarVO {
    private String tipoSituacaoMilitar;
    private String certificadoMilitar;
    private String serieCertificadoMilitar;
    private String categoriaCertificadoMilitar;
    private Date dataEmissao;
    private String orgaoEmissao;

    public SituacaoMilitarVO() {
    }

    public String getTipoSituacaoMilitar() {
        return tipoSituacaoMilitar;
    }

    public void setTipoSituacaoMilitar(String tipoSituacaoMilitar) {
        this.tipoSituacaoMilitar = tipoSituacaoMilitar;
    }

    public String getCertificadoMilitar() {
        return certificadoMilitar;
    }

    public void setCertificadoMilitar(String certificadoMilitar) {
        this.certificadoMilitar = certificadoMilitar;
    }

    public String getSerieCertificadoMilitar() {
        return serieCertificadoMilitar;
    }

    public void setSerieCertificadoMilitar(String serieCertificadoMilitar) {
        this.serieCertificadoMilitar = serieCertificadoMilitar;
    }

    public String getCategoriaCertificadoMilitar() {
        return categoriaCertificadoMilitar;
    }

    public void setCategoriaCertificadoMilitar(String categoriaCertificadoMilitar) {
        this.categoriaCertificadoMilitar = categoriaCertificadoMilitar;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getOrgaoEmissao() {
        return orgaoEmissao;
    }

    public void setOrgaoEmissao(String orgaoEmissao) {
        this.orgaoEmissao = orgaoEmissao;
    }
}
