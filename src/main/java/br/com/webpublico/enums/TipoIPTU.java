package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoIPTU implements EnumComDescricao {
    PREDIAL(1,"I.P.T.U Predial"),
    TERRITORIAL(2,"I.P.T.U Territorial"),
    TAXA_COLETA_LIXO(3,"Taxa de Coleta de Lixo"),
    TAXA_ILULIMINACAO_PUBLICA(4,"Taxa De Iluminação Pública");

    private Integer codigo;
    private String descricao;

    TipoIPTU(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
