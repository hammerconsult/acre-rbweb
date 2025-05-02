package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
public enum TipoNotaFiscalServicoNfseDTO implements NfseEnumDTO {
    ELETRONICA("Eletrônica"),
    CONVENCIONAL("Convencional"),
    NAO_EMITE("Não Emite");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoNotaFiscalServicoNfseDTO(String descricao) {
        this.descricao = descricao;
    }
}
