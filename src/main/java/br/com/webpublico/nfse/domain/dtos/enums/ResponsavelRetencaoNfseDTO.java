package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
public enum ResponsavelRetencaoNfseDTO implements NfseEnumDTO {
    TOMADADOR("Tomador"),
    INTERMEDIARIO("Intermediário");

    String descricao;

    ResponsavelRetencaoNfseDTO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
