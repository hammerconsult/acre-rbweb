package br.com.webpublico.enums;

public enum SituacaoAquisicao {

    EM_ELABORACAO("Em Elaboração"),
    AGUARDANDO_EFETIVACAO("Aguardando Efetivação"),
    FINALIZADO("Finalizado"),
    ESTORNADO("Estornado"),
    RECUSADO("Recusado");

    private String descricao;

    SituacaoAquisicao(String descricao) {
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
