package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoCadastroImobiliario implements EnumComDescricao {

    ATIVO("Ativo"),
    INATIVO("Inativo");

    private String descricao;

    SituacaoCadastroImobiliario(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
