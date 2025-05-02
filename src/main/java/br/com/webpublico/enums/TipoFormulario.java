package br.com.webpublico.enums;

/**
 * Created by Wellington Abdo on 04/01/2017.
 */
public enum TipoFormulario {
    COTACAO("Cotação"), PLANILHAORCAMENTARIA("Planilha Orçamentária");

    private String descricao;

    TipoFormulario(String descricao) {
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
