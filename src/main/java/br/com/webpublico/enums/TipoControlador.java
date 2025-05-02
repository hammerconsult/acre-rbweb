package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 06/05/14
 * Time: 08:49
 * To change this template use File | Settings | File Templates.
 */
public enum TipoControlador {

    ADICIONAR_FORNECEDOR("Adicionar Fornecedor"),
    HABILITAR_FORNECEDOR("Habilitar Fornecedor"),
    HABILITAR_PREGAO("Habilitar Pregão");

    private String descricao;

    private TipoControlador(String descricao) {
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
