package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.tributario.TipoImovelDTO;

public enum TipoImovel implements EnumComDescricao {
    PREDIAL("1-Predial ", TipoImovelDTO.PREDIAL),
    TERRITORIAL("2-Territorial", TipoImovelDTO.TERRITORIAL);
    private String descricao;
    private TipoImovelDTO toDto;

    TipoImovel(String descricao, TipoImovelDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoImovelDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
