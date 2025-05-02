package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Criado por Mateus
 * Data: 18/04/2017.
 */
public class PensaoAlimenticiaVO {
    private Date inicioVigencia;
    private Date fimVigencia;
    private String verba;
    private String tipoValorPensaoAlimenticia;
    private String instituidor;
    private String beneficiario;

    public PensaoAlimenticiaVO() {
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

    public String getVerba() {
        return verba;
    }

    public void setVerba(String verba) {
        this.verba = verba;
    }

    public String getTipoValorPensaoAlimenticia() {
        return tipoValorPensaoAlimenticia;
    }

    public void setTipoValorPensaoAlimenticia(String tipoValorPensaoAlimenticia) {
        this.tipoValorPensaoAlimenticia = tipoValorPensaoAlimenticia;
    }

    public String getInstituidor() {
        return instituidor;
    }

    public void setInstituidor(String instituidor) {
        this.instituidor = instituidor;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }
}
