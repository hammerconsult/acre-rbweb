package br.com.webpublico.enums.rh;

import br.com.webpublico.webreportdto.dto.rh.OpcoesFiltroRelatorioDTO;

public enum OpcoesFiltroRelatorio {
    ATIVO("Ativo", OpcoesFiltroRelatorioDTO.ATIVO),
    INATIVO("Inativo", OpcoesFiltroRelatorioDTO.INATIVO),
    TODOS("Todos", OpcoesFiltroRelatorioDTO.TODOS);

    OpcoesFiltroRelatorio(String descricao, OpcoesFiltroRelatorioDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    private String descricao;
    private OpcoesFiltroRelatorioDTO toDto;

    public String getDescricao() {
        return descricao;
    }

    public OpcoesFiltroRelatorioDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
