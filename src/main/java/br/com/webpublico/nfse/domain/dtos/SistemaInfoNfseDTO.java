package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;

public class SistemaInfoNfseDTO implements Serializable {

    private String versao;

    public SistemaInfoNfseDTO() {
    }

    public SistemaInfoNfseDTO(String versao) {
        this.versao = versao;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }
}
