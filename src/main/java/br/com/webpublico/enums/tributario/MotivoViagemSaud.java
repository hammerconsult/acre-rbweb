
package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum MotivoViagemSaud implements EnumComDescricao {

    TRATAMENTO_SAUDE("Tratamento de saúde"),
    EDUCACAO("Educação"),
    TURISMO_LAZER("Cultura, Turismo, Desporto e Lazer"),
    OUTRO("Outros");

    private final String descricao;

    MotivoViagemSaud(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
