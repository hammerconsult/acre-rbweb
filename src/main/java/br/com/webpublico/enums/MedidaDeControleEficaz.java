package br.com.webpublico.enums;

/**
 * Created by carlos on 04/09/15.
 */
public enum MedidaDeControleEficaz {
    SIM("Sim"),
    NAO("Não");
    private String descricao;

    MedidaDeControleEficaz(String descricao) {
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
