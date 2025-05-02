package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
public enum TipoContribuinteNfseDTO implements NfseEnumDTO {
    EVENTUAL("Eventual"),
    PERMANENTE("Permanente");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoContribuinteNfseDTO(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
