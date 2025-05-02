package br.com.webpublico.enums;

public enum TipoUsuarioGestor {

    CONTABIL("Gestor Contábil"),
    CONTROLADORIA("Gestor da Controladoria");

    private String descricao;

    TipoUsuarioGestor(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isGestorContabil() {
        return CONTABIL.equals(this);
    }

    public boolean isGestorControladoria() {
        return CONTROLADORIA.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
