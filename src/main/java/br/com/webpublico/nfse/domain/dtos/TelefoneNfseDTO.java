package br.com.webpublico.nfse.domain.dtos;

public class TelefoneNfseDTO implements NfseDTO {

    private String tipoTelefone;
    private String telefone;

    public String getTipoTelefone() {
        return tipoTelefone;
    }

    public void setTipoTelefone(String tipoTelefone) {
        this.tipoTelefone = tipoTelefone;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
