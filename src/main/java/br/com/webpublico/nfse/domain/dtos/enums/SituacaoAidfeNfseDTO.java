package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public enum SituacaoAidfeNfseDTO implements NfseEnumDTO {
    AGUARDANDO("Aguardando"),
    DEFERIDA("Deferida"),
    PARCIALMENTE("Deferida Parcialmente"),
    INDEFERIDA("Indeferida");

    private String descricao;

    SituacaoAidfeNfseDTO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
