package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: André Gustavo
 * Date: 30/01/14
 * Time: 09:26
 */
public enum SituacaoLogradouro {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private String descricao;

    private SituacaoLogradouro(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
