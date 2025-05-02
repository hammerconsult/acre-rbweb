package br.com.webpublico.pncp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CriterioJulgamentoPncp {

    MENOR_PRECO(1, "Menor preço"),
    MAIOR_DESCONTO(2, "Maior desconto"),
    MELHOR_TECNICA_OU_CONTEUDO_ARTISTICO(3, "Melhor técnica ou conteúdo artístico (indisponível)"),
    TECNICA_E_PRECO(4, "Técnica e preço"),
    MAIOR_LANCE(5, "Maior lance"),
    MAIOR_RETORNO_ECONOMICO(6, "Maior retorno econômico"),
    NAO_SE_APLICA(7, "Não se aplica"),
    MELHOR_TECNICA(8, "Melhor técnica"),
    CONTEUDO_ARTISTICO(9, "Conteúdo artístico");

    private final Integer codigo;
    private final String descricao;

    CriterioJulgamentoPncp(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return codigo.toString();
    }

    @JsonValue
    public Integer toValue() {
        return codigo;
    }
}
