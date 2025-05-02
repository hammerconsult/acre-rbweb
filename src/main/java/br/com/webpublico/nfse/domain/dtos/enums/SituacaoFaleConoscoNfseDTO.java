package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
public enum SituacaoFaleConoscoNfseDTO implements NfseEnumDTO {
    ABERTO("Aberto"), ENCERRADO("Encerrado");

    private String descricao;

    SituacaoFaleConoscoNfseDTO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
