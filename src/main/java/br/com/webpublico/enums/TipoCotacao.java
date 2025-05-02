package br.com.webpublico.enums;

/**
 * Created by Wellington Abdo on 29/09/2017.
 */
public enum TipoCotacao {
    FORNECEDOR("Fornecedor"), VALOR_REFERENCIA("Valor de Referência");

    private String descricao;

    TipoCotacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isFornecedor() {
        return FORNECEDOR.equals(this);
    }

    public boolean isValorReferencia() {
        return VALOR_REFERENCIA.equals(this);
    }
}
