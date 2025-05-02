package br.com.webpublico.entidades;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 13/08/14
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
public enum TipoCalculoAposentadoria {

    PROPORCIONAL("Proporcional"),
    INTEGRAL("Integral");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    private TipoCalculoAposentadoria(String descricao) {
        this.descricao = descricao;
    }
}
