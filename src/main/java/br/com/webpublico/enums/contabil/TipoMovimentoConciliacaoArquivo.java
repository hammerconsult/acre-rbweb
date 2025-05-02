package br.com.webpublico.enums.contabil;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoMovimentoConciliacaoArquivo implements EnumComDescricao {
    TRANSFERENCIA_FINANCEIRA("Transferencia Financeira", "<TRNTYPE>XFER"),
    DEBITO("Débito", "<TRNTYPE>DEBIT"),
    CREDITO("Crédito", "<TRNTYPE>CREDIT"),
    DEPOSITO("Déposito", "<TRNTYPE>DEP");
    private String descricao;
    private String identificadorArquivo;

    TipoMovimentoConciliacaoArquivo(String descricao, String identificadorArquivo) {
        this.descricao = descricao;
        this.identificadorArquivo = identificadorArquivo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public String getIdentificadorArquivo() {
        return identificadorArquivo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
