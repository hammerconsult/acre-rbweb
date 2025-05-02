package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public enum SituacaoSolicitacaoRPSNfseDTO implements NfseEnumDTO {
    AGUARDANDO("Aguardando"),
    DEFERIDA("Deferida"),
    INDEFERIDA("Indeferida");

    private String descricao;

    SituacaoSolicitacaoRPSNfseDTO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
