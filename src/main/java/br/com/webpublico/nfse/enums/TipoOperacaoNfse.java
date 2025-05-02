package br.com.webpublico.nfse.enums;

import br.com.webpublico.nfse.domain.dtos.enums.TipoOperacaoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseDTO;

public enum TipoOperacaoNfse implements NfseDTO {
    VENDAS(TipoOperacaoNfseDTO.VENDAS),
    PRESTACAO_DE_SERVICOS(TipoOperacaoNfseDTO.PRESTACAO_DE_SERVICOS);

    private TipoOperacaoNfseDTO dto;

    TipoOperacaoNfse(TipoOperacaoNfseDTO dto) {
        this.dto = dto;
    }

    public static TipoOperacaoNfse fromDto(TipoOperacaoNfseDTO dto) {
        for (int i = 0; i < values().length; i++) {
            TipoOperacaoNfse value = values()[i];
            if (value.toDto().equals(dto)) {
                return value;
            }
        }
        return null;
    }

    public TipoOperacaoNfseDTO toDto() {
        return dto;
    }
}
