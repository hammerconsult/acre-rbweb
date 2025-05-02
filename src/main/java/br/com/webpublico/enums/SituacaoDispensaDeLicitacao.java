package br.com.webpublico.enums;

/**
 * Created by William on 12/04/2018.
 */
public enum SituacaoDispensaDeLicitacao {
    EM_ELABORACAO("Em elaboração", "Em andamento"),
    CONCLUIDO("Concluído", "Ratificada");

    private String descricao;

    private String situacaoPncp;

    SituacaoDispensaDeLicitacao(String descricao, String situacaoPncp) {
        this.descricao = descricao;
        this.situacaoPncp = situacaoPncp;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getSituacaoPncp() {
        return situacaoPncp;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isConcluido() {
        return CONCLUIDO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
