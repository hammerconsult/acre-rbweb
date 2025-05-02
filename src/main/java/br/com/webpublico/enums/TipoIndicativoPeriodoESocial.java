package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoIndicativoPeriodoESocial implements EnumComDescricao {

    MENSAL("Mensal", 1),
    ANUAL("Anual(13º salário)", 2);

    private String descricao;
    private Integer codigo;

    TipoIndicativoPeriodoESocial(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }
}
