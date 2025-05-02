package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public enum TipoPeriodoValorEstimadoNfseDTO implements NfseEnumDTO {

    MENSAL("Mensal"),
    ANUAL("Anual");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoPeriodoValorEstimadoNfseDTO(String descricao) {
        this.descricao = descricao;
    }
}
