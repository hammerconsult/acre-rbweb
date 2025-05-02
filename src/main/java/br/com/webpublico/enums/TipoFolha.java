package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 25/07/13
 * Time: 10:35
 * To change this template use File | Settings | File Templates.
 */
public enum TipoFolha {
    PAISAGEM("Paisagem"),
    RETRATO("Retrato");
    private String descricao;

    private TipoFolha(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
