package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoAlteracaoCadastralPessoa implements EnumComDescricao {

    EM_ABERTO("Em Aberto"),
    APROVADO("Deferido"),
    INDEFERIDO("Indeferido");

    String descricao;

    private SituacaoAlteracaoCadastralPessoa(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
