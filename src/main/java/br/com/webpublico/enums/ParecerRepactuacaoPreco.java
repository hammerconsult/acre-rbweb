package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 14/04/14
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
public enum ParecerRepactuacaoPreco {

    ACEITO("Aceito"),
    NAO_ACEITO("Não Aceito");

    private String descricao;

    private ParecerRepactuacaoPreco(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
