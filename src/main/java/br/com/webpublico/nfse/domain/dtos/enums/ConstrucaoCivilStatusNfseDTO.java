package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
public enum ConstrucaoCivilStatusNfseDTO implements NfseEnumDTO {
    APROVADA("Aprovada"),
    NAO_INICIADA("Não iniciada"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída"),
    PARALIZADA("Paralizada");

    private String descricao;

    ConstrucaoCivilStatusNfseDTO(String descricao) {
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
