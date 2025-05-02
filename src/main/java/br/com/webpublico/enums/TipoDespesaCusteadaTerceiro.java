package br.com.webpublico.enums;

public enum TipoDespesaCusteadaTerceiro {
    SOMENTE_HOSPEDAGEM("Somente Hospedagem"),
    SOMENTE_HOSPEDAGEM_E_ALIMENTACAO("Somente Hospedagem e Alimentação"),
    SOMENTE_PASSAGEM("Somente Passagem");

    private String descricao;

    TipoDespesaCusteadaTerceiro(String descricao) {
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
