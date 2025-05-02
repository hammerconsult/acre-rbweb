package br.com.webpublico.enums.tributario;

public enum TipoNumeroDoctoOficial {
    GRUPO("Grupo"),
    TIPO("Tipo"),
    EXERCICIO_GRUPO("Exercício e Grupo"),
    EXERCICIO_TIPO("Exercício e Tipo");

    private final String descricao;

    TipoNumeroDoctoOficial(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isGrupo() {
        return this.equals(GRUPO);
    }

    public boolean isTipo() {
        return this.equals(TIPO);
    }

    public boolean isExercicioGrupo() {
        return this.equals(EXERCICIO_GRUPO);
    }

    public boolean isExercicioTipo() {
        return this.equals(EXERCICIO_TIPO);
    }
}
