package br.com.webpublico.enums;

/**
 * Created by William on 12/04/2016.
 */
public enum TipoRepresentatividadeSiprev {
    TUTOR_NATO("TUTOR NATO", "1"),
    TUTOR("TUTOR", "2"),
    CURADOR("CURADOR", "3"),
    ADMINISTRADOR_PROVISORIO("ADMINISTRADOR PROVISÓRIO", "4");

    private String descricao;
    private String codigo;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    private TipoRepresentatividadeSiprev(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
