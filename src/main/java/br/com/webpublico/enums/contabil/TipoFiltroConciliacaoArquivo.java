package br.com.webpublico.enums.contabil;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoFiltroConciliacaoArquivo implements EnumComDescricao {
    DATA("Data"),
    VALOR("Valor"),
    NUMERO_DOCUMENTO("Número do Documento");
    private String descricao;

    TipoFiltroConciliacaoArquivo(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
