package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoContratoCalamidade implements EnumComDescricao {

    ATRASO("Atraso"),
    INEXECUCAO_TOTAL("Inexecução total"),
    INEXECUCAO_PARCIAIS("Inexecução Parciais"),
    CONFORME("Conforme");
    private String descricao;

    SituacaoContratoCalamidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
