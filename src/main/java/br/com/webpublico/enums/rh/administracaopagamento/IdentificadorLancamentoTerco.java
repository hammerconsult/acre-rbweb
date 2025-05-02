package br.com.webpublico.enums.rh.administracaopagamento;

public enum IdentificadorLancamentoTerco {
    AUTOMATICO("Lançamento efetuado pela rotina mensal"),
    REPROCESSADO("Lançamento efetuado pela rotina de reprocessamento");

    private String descricao;

    IdentificadorLancamentoTerco(String descricao) {
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
