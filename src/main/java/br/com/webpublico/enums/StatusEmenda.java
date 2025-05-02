package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum StatusEmenda implements EnumComDescricao {
    ABERTO("Aberto"),
    APROVADO("Aprovado"),
    REJEITADO("Rejeitado");

    private String descricao;

    StatusEmenda(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAberto() {
        return ABERTO.equals(this);
    }

    public boolean isAprovado() {
        return APROVADO.equals(this);
    }

    public boolean isRejeitado() {
        return REJEITADO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
