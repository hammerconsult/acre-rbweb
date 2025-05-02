package br.com.webpublico.enums;

public enum TipoTransferenciaBem {

    ORIGINAL("Original"),
    DEPRECIACAO("Depreciação"),
    AMORTIZACAO("Amortização"),
    AJUSTE("Ajuste de Perdas");
    private String descricao;

    TipoTransferenciaBem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
