package br.com.webpublico.enums;

/**
 * Created by William on 08/03/2017.
 */
public enum TipoPassageiro {
    ESTUDANTE("Estudante"),
    DEMAIS_USUARIOS("Demais Usuários");

    private String descricao;

    TipoPassageiro(String descricao) {
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
