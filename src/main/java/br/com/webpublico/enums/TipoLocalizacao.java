package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoLocalizacao implements EnumComDescricao {

    AREA_DE_SITIOS_E_FAZENDA("Área de sítios e fazenda"),
    AREA_ESTRITAMENTE_RESIDENCIAL_HOSPITAIS_ESCOLAS("Áreas estritamente residencial, de hospitais ou escolas"),
    AREA_MISTA_PREDOMINANTEMENTE_RESIDENCIAL("Área mista, predominantemente residencial"),
    AREA_MISTA_COM_VOCACAO_COMERCIAL_E_ADMINISTRATIVA("Área mista, com vocação comercial e administrativa"),
    AREA_MISTA_COM_VOCACAO_RECREACIONAL("Área mista, com vocação recreacional"),
    AREA_PREDOMINANTEMENTE_INDUSTRIAL("Área predominantemente industrial");

    private String descricao;

    TipoLocalizacao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
