package br.com.webpublico.enums;

/**
 * Criado por Mateus
 * Data: 23/08/2016.
 */
public enum SituacaoJudicial {

    AJUIZADA("Ajuizada"),
    AGUARDANDO_AJUIZAMENTO("Aguardando ajuizamento"),
    NAO_AJUIZADA("Não ajuizada");

    SituacaoJudicial(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
