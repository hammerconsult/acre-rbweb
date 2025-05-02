package br.com.webpublico.nfse.enums;

import br.com.webpublico.nfse.domain.dtos.enums.TipoProfissionalNfseDTO;

public enum TipoProfissional {
    ENGENHEIRO("Engenheiro", TipoProfissionalNfseDTO.ENGENHEIRO),
    ARQUITETO("Arquiteto", TipoProfissionalNfseDTO.ARQUITETO);

    private String descricao;
    private TipoProfissionalNfseDTO dto;

    TipoProfissional(String descricao, TipoProfissionalNfseDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public static TipoProfissional findByDTO(TipoProfissionalNfseDTO dto) {
        for (int i = 0; i < values().length; i++) {
            TipoProfissional value = values()[i];
            if (value.dto.equals(dto)) {
                return value;
            }
        }
        return null;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoProfissionalNfseDTO getDto() {
        return dto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
