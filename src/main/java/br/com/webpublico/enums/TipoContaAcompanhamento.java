package br.com.webpublico.enums;

/**
 * Created by carlos on 22/06/17.
 */
public enum TipoContaAcompanhamento {
    RECEBIDA("Recebida"),
    RETIRADA("Retirada");
    private String descricao;

    TipoContaAcompanhamento(String descricao) {
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
