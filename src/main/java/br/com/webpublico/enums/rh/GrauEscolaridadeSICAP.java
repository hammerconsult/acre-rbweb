package br.com.webpublico.enums.rh;

public enum GrauEscolaridadeSICAP {
    NIVEL_FUNDAMENTAL("1", "Nível Fundamental"),
    NIVEL_MEDIO("2", "Nível Médio"),
    NIVEL_SUPERIOR("3", "Nível Superior"),
    NIVEL_TECNICO("4", "Nível Técnico"),
    NAO_APLICAVEL("9", "Não Aplicável");

    GrauEscolaridadeSICAP(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    private String codigo;
    private String descricao;

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
