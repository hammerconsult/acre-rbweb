package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 12/07/14
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
public enum ModuloFAQ {

    CONTABIL("Orçamento/Financeiro e Contábil"),
    GERAL("Geral"),
    RH("Recursos Humanos"),
    TRIBUTARIO("Tributário");

    private String descricao;

    private ModuloFAQ(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
