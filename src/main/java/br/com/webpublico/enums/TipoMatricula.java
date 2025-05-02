package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 28/08/13
 * Time: 19:12
 * To change this template use File | Settings | File Templates.
 */
public enum TipoMatricula {
    AVERBACAO_DA_ESCRITURA("1-Averbação da Escritura"),
    TITULO_DEFINITIVO_DE_POSSE("2-Título Definitivo de Posse");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    private TipoMatricula(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
