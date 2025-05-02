package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoRemessaProtesto implements EnumComDescricao {
    ENVIADO("Enviado Com Sucesso"),
    RECUSADO("Recusado");

    private final String descricao;

    SituacaoRemessaProtesto(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
