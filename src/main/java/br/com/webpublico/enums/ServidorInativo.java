package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 23/12/13
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public enum ServidorInativo {
    TODOS("Todos"),
    APOSENTADO("Aposentado"),
    PENSIONISTA("Pensionista");


    private String descricao;

    private ServidorInativo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
