package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum OrigemRemessaProtesto implements EnumComDescricao {
    MANUAL("Manual"),
    AGENDAMENTO("Agendamento de Tarefas");

    private final String descricao;

    OrigemRemessaProtesto(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
