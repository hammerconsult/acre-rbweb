package br.com.webpublico.enums;

import java.io.Serializable;

/**
 * Created by AndreGustavo on 26/11/2014.
 */
public enum SituacaoCapacitacao implements Serializable {
    EM_ANDAMENTO("Em Andamento"),
    CANCELADO("Cancelado"),
    CONCLUIDO("Concluído");

    private String descricao;

    SituacaoCapacitacao(String descricao) {
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
