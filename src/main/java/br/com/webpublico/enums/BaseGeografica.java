package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.BaseGeograficaDTO;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 16/08/13
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public enum BaseGeografica {
    ESTADUAL("Estadual", BaseGeograficaDTO.ESTADUAL),
    MUNICIPAL("Municipal", BaseGeograficaDTO.MUNICIPAL),
    REGIONAL_FEDERAL("Regional/Federal", BaseGeograficaDTO.REGIONAL_FEDERAL);
    private String descricao;
    private BaseGeograficaDTO toDto;

    BaseGeografica(String descricao, BaseGeograficaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public BaseGeograficaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
