package br.com.webpublico.controlerelatorio;

import br.com.webpublico.webreportdto.dto.rh.OpcoesFiltroRelatorioServidoresPASEPDTO;

public enum OpcoesFiltroRelatorioServidoresPASEP {

    ATIVO("Ativo",OpcoesFiltroRelatorioServidoresPASEPDTO.ATIVO),
    INATIVO("Inativo", OpcoesFiltroRelatorioServidoresPASEPDTO.INATIVO),
    INATIVO_OBITO("Inativos Óbito",  OpcoesFiltroRelatorioServidoresPASEPDTO.INATIVO_OBITO),
    EXONERADOS("Todos Exonerados", OpcoesFiltroRelatorioServidoresPASEPDTO.EXONERADOS),
    TODOS("Todos", OpcoesFiltroRelatorioServidoresPASEPDTO.TODOS);

    private String descricao;
    private OpcoesFiltroRelatorioServidoresPASEPDTO toDto;

    OpcoesFiltroRelatorioServidoresPASEP(String descricao, OpcoesFiltroRelatorioServidoresPASEPDTO toDto) {
        this.toDto = toDto;
        this.descricao = descricao;
    }

    public OpcoesFiltroRelatorioServidoresPASEPDTO getToDto() {
        return toDto;
    }

    public String getDescricao() {
        return descricao;
    }

}
