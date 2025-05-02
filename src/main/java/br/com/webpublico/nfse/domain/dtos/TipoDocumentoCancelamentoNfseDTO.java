package br.com.webpublico.nfse.domain.dtos;

public enum TipoDocumentoCancelamentoNfseDTO {
    NOTA_FISCAL("Nota Fiscal"),
    SERVICO_DECLARADO("Serviço Declarado");

    private String descricao;

    TipoDocumentoCancelamentoNfseDTO(String descricao) {
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
