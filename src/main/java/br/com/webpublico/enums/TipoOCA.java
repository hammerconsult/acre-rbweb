package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.TipoOCADTO;

public enum TipoOCA implements EnumComDescricao {
    EXCLUSIVO("EX - Exclusivo", TipoOCADTO.EXCLUSIVO),
    NAO_EXCLUSIVO("NEX - Não Exclusivo", TipoOCADTO.NAO_EXCLUSIVO);
    private String descricao;
    private TipoOCADTO toDto;

    TipoOCA(String descricao, TipoOCADTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoOCADTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
