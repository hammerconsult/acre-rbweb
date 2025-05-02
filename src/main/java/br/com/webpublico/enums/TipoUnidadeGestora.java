package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoUnidadeGestoraDTO;

public enum TipoUnidadeGestora {
    PRESTACAO_CONTAS("Prestação de Contas", TipoUnidadeGestoraDTO.PRESTACAO_CONTAS),
    MSC("MSC", TipoUnidadeGestoraDTO.MSC),
    ADMINISTRATIVO("Administrativo", TipoUnidadeGestoraDTO.ADMINISTRATIVO),
    RELATORIO("Relatório", TipoUnidadeGestoraDTO.RELATORIO);
    private String descricao;
    private TipoUnidadeGestoraDTO dto;


    TipoUnidadeGestora(String descricao, TipoUnidadeGestoraDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public TipoUnidadeGestoraDTO getDto() {
        return dto;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
