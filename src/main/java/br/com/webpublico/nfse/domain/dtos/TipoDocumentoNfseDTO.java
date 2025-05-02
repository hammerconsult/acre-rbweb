package br.com.webpublico.nfse.domain.dtos;

public enum TipoDocumentoNfseDTO {
    NFSE("Nfs-e"),
    DESIF("Desif."),
    SERVICO_DECLARADO("Serv. Dec.");
    private String descricao;

    TipoDocumentoNfseDTO(String descricao) {
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
