package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Wanderley
 * Date: 28/11/13
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
public enum AgrupadorCobrancaAdm {

    CONTRIBUINTE("Contribuinte"),
    CADASTRO("Cadastro");


    private String descricao;

    private AgrupadorCobrancaAdm(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
