package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoCadastroTerreno implements EnumComDescricao {

    TERRITORIAL("Territorial"),
    PREDIAL("Predial");
    private String descricao;

    private TipoCadastroTerreno(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
