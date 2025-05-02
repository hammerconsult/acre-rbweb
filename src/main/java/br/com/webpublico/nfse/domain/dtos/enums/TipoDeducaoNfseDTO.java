package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public enum TipoDeducaoNfseDTO implements NfseEnumDTO {
    MATERIAL("M"),
    SUB_EMPREITADA("S"),
    ALUGUEL_DE_EQUIPAMENTOS(null);

    private String tipoDeducao;

    TipoDeducaoNfseDTO(String tipoDeducao) {
        this.tipoDeducao = tipoDeducao;
    }

    public String getTipoDeducao() {
        return tipoDeducao;
    }
}
