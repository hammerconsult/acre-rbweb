package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by carlos on 05/04/17.
 */
public class TelefoneVO {
    private String telefone;
    private String tipoTelefone;
    private Date dataRegistro;
    private String principal;

    public TelefoneVO() {
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipoTelefone() {
        return tipoTelefone;
    }

    public void setTipoTelefone(String tipoTelefone) {
        this.tipoTelefone = tipoTelefone;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }
}
