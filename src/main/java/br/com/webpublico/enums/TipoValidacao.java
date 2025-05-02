package br.com.webpublico.enums;

/**
 * Created by mateus on 07/05/18.
 */
public enum TipoValidacao {
    DESPESA_ORCAMENTARIA("Despesa Orçamentária"),
    MOVIMENTO_DIVIDA_PUBLICA("Movimento Dívida Publica"),
    RECEITA_ORCAMENTARIA("Receita Orçamentária");

    private String descricao;

    TipoValidacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
