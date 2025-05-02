package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 25/07/13
 * Time: 10:36
 * To change this template use File | Settings | File Templates.
 */
public enum TipoFuncaoAgrupador {
    MENOR("Menor"),
    MEDIA("Média"),
    MAIOR("Maior"),
    SOMAR("Somar");
    private String descricao;

    private TipoFuncaoAgrupador(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
