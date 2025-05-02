package br.com.webpublico.nfse.enums;

import br.com.webpublico.nfse.domain.dtos.enums.TipoServicoDeclaradoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseDTO;

public enum TipoServicoDeclarado implements NfseDTO {
    SERVICO_PRESTADO(TipoServicoDeclaradoNfseDTO.SERVICO_PRESTADO), SERVICO_TOMADO(TipoServicoDeclaradoNfseDTO.SERVICO_TOMADO);

    private TipoServicoDeclaradoNfseDTO dto;

    TipoServicoDeclarado(TipoServicoDeclaradoNfseDTO dto) {
        this.dto = dto;
    }

    public static TipoServicoDeclarado fromDto(TipoServicoDeclaradoNfseDTO dto) {
        for (int i = 0; i < values().length; i++) {
            TipoServicoDeclarado value = values()[i];
            if (value.toDto().equals(dto)) {
                return value;
            }
        }
        return null;
    }

    public TipoServicoDeclaradoNfseDTO toDto() {
        return dto;
    }
}
