package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public enum TipoAnexoTipoManualDTO implements NfseEnumDTO {
    ARQUIVO("Arquivo"), VIDEO("Vídeo");

    private String descricao;

    TipoAnexoTipoManualDTO(String descricao) {

        this.descricao = descricao;
    }
}
