package br.com.webpublico.enums.rh;

import br.com.webpublico.webreportdto.dto.rh.VerbasDTO;

public enum Verbas {

    TODAS("Verbas da Folha", VerbasDTO.TODAS),
    ESPECIFICAR("Especificar Verba", VerbasDTO.ESPECIFICAR);

    private String descricao;
    private VerbasDTO toDto;

    Verbas(String descricao, VerbasDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public VerbasDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
