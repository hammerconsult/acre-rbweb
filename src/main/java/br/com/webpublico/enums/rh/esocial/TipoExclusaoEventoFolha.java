package br.com.webpublico.enums.rh.esocial;

public enum TipoExclusaoEventoFolha {
    MENSAL("Mensal"),
    SALARIO_13("Décimo Terceiro");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    TipoExclusaoEventoFolha(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
