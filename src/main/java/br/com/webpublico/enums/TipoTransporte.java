package br.com.webpublico.enums;

/**
 * Created by israeleriston on 10/11/15.
 */
public enum TipoTransporte {
    MOTO("Moto"),
    CARRO("Carro"),
    BICICLETA("Bicicleta");

    private String descricao;

    TipoTransporte(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
