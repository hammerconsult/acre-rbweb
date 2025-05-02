package br.com.webpublico.entidadesauxiliares;

public class FichaCredorTelefone {

    private String numeroTelefone;
    private String tipoTelefone;
    private Boolean telefonePrincipal;
    private String pessoaContato;

    public FichaCredorTelefone() {
    }

    public String getNumeroTelefone() {
        return numeroTelefone;
    }

    public void setNumeroTelefone(String numeroTelefone) {
        this.numeroTelefone = numeroTelefone;
    }

    public String getTipoTelefone() {
        return tipoTelefone;
    }

    public void setTipoTelefone(String tipoTelefone) {
        this.tipoTelefone = tipoTelefone;
    }

    public Boolean getTelefonePrincipal() {
        return telefonePrincipal;
    }

    public void setTelefonePrincipal(Boolean telefonePrincipal) {
        this.telefonePrincipal = telefonePrincipal;
    }

    public String getPessoaContato() {
        return pessoaContato;
    }

    public void setPessoaContato(String pessoaContato) {
        this.pessoaContato = pessoaContato;
    }
}
