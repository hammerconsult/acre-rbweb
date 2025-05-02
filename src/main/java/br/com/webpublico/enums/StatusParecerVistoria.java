package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 17/01/14
 * Time: 11:29
 * To change this template use File | Settings | File Templates.
 */
public enum StatusParecerVistoria {
    FAVORAVEL("Favorável"),
    NAO_FAVORAVEL("Não Favorável");
    private String descricao;

    private StatusParecerVistoria(String descricao) {
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
