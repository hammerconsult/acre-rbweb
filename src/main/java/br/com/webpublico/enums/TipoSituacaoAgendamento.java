package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoSituacaoAgendamento implements EnumComDescricao {
    AGUARDANDO("Aguardando Execução"),
    EXECUTADO("Executado"),
    ERRO("Erro ao Executar");
    private String descricao;

    TipoSituacaoAgendamento(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public boolean isAguardando() {
        return TipoSituacaoAgendamento.AGUARDANDO.equals(this);
    }
}
