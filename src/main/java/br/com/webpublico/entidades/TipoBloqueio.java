package br.com.webpublico.entidades;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 25/08/14
 * Time: 08:40
 * To change this template use File | Settings | File Templates.
 */
public enum TipoBloqueio {

    VALE_TRANSPORTE("Vale Transporte");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoBloqueio(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
