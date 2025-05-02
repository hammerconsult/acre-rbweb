package br.com.webpublico.enums;

public enum SituacaoVerificacaoBemMovel {

    EM_ELABORACAO("Em Elaboração"),
    FINALIZADA("Finalizada");

    private String descricao;

    SituacaoVerificacaoBemMovel(String descricao) {
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
