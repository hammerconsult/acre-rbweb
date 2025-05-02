package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 05/08/13
 * Time: 10:53
 * To change this template use File | Settings | File Templates.
 */
public enum OpcaoReadaptacao {


    FISICA("Doença Física"),
    PSIQUICA("Doença Psíquica"),
    PERMANENTE("Parcial Permanente"),
    TEMPORARIO("Parcial Temporária");

    private String descricao;

    OpcaoReadaptacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
