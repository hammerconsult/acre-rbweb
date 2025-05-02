package br.com.webpublico.nfse.enums;

public enum PeriodicidadeTarifaBancaria {
    POR_EVENTO("Por Evento"), CADA_1_DIA("A cada 1 dias"),
    CADA_30_DIAS("A cada 30 dias"), CADA_180_DIAS("A cada 180 dias");

    private String descricao;

    PeriodicidadeTarifaBancaria(String descricao) {
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
