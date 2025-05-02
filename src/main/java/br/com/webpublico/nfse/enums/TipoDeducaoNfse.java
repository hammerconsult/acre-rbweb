package br.com.webpublico.nfse.enums;

import br.com.webpublico.nfse.domain.dtos.enums.TipoDeducaoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseDTO;

public enum TipoDeducaoNfse implements NfseDTO {
    MATERIAL(TipoDeducaoNfseDTO.MATERIAL),
    SUB_EMPREITADA(TipoDeducaoNfseDTO.SUB_EMPREITADA),
    ALUGUEL_DE_EQUIPAMENTOS(TipoDeducaoNfseDTO.ALUGUEL_DE_EQUIPAMENTOS);

    private TipoDeducaoNfseDTO dto;

    TipoDeducaoNfse(TipoDeducaoNfseDTO dto) {
        this.dto = dto;
    }

    public static TipoDeducaoNfse fromDto(TipoDeducaoNfseDTO dto) {
        for (int i = 0; i < values().length; i++) {
            TipoDeducaoNfse value = values()[i];
            if (value.toDto().equals(dto)) {
                return value;
            }
        }
        return null;
    }

    public TipoDeducaoNfseDTO toDto() {
        return dto;
    }
}
