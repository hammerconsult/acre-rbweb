package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 20/08/14
 * Time: 09:54
 * To change this template use File | Settings | File Templates.
 */
public enum TipoAdesao {

    INTERNA("Interna"),
    EXTERNA("Externa");

    private String descricao;

    private TipoAdesao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
