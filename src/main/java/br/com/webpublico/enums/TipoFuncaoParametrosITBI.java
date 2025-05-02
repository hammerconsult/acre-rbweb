package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoFuncaoParametrosITBI implements EnumComDescricao {
    RESPONSAVEL_COMISSAO_AVALIADORA("Responsável pela Comissão Avaliadora"),
    DIRETOR_CHEFE_DEPARTAMENTO_TRIBUTO("Diretor Chefe do Departamento de Tributos"),
    SUPORTE_TECNICO("Suporte Técnico");

    private final String descricao;

    TipoFuncaoParametrosITBI(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
