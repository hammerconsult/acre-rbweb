package br.com.webpublico.nfse.enums;

public enum SituacaoArquivoDesif {
    AGUARDANDO("Aguardando"),
    EM_PROCESSAMENTO("Em processamento"),
    PROCESSADO("Processado"),
    INCONSISTENTE("Inconsistênte");


    private String descricao;

    SituacaoArquivoDesif(String descricao) {
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
