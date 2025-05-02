package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 28/05/14
 * Time: 13:48
 * To change this template use File | Settings | File Templates.
 */
public enum TipoTomadorPrestadorNF {
    PESSOA("Contribuinte Geral"),
    ECONOMICO("Cadastro Econômico");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    private TipoTomadorPrestadorNF(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
