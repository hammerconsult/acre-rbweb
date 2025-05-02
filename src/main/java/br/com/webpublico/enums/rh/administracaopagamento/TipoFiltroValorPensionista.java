package br.com.webpublico.enums.rh.administracaopagamento;

public enum TipoFiltroValorPensionista {
    MES("Mês"),
    INICIO("Início"),
    FINAL("Final"),
    CALCULO("Cálculo");
    private String descricao;

    TipoFiltroValorPensionista(String descricao) {
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
