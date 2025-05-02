package br.com.webpublico.enums;

/**
 * Created by Buzatto on 31/08/2015.
 */
public enum SituacaoRecurso {

    EM_ANDAMENTO("Em Andamento"),
    ACEITO("Aceito"),
    NAO_ACEITO("Não Aceito");

    private final String descricao;

    SituacaoRecurso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
