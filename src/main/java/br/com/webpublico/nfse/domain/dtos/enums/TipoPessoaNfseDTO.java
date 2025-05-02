package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public enum TipoPessoaNfseDTO implements NfseEnumDTO {
    FISICA("Física"), JURIDICA("Jurídica");

    private String descricao;

    TipoPessoaNfseDTO(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
