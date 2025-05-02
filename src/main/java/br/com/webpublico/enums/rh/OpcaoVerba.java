package br.com.webpublico.enums.rh;

import br.com.webpublico.webreportdto.dto.rh.OpcaoVerbaDTO;

public enum OpcaoVerba {

    ENTRADA("Verbas que entraram na folha", OpcaoVerbaDTO.ENTRADA),
    SAIDA("Verbas que saíram da folha", OpcaoVerbaDTO.SAIDA);

    private String descricao;
    private OpcaoVerbaDTO toDto;

    OpcaoVerba(String descricao, OpcaoVerbaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public OpcaoVerbaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
