package br.com.webpublico.nfse.enums;

/**
 * Created by william on 19/09/17.
 */
public enum TipoDocumentoNfse {
    NFSE("NFS-e"),
    DESIF("DES-IF"),
    SERVICO_DECLARADO("Serviço Declarado");
    private String descricao;

    TipoDocumentoNfse(String descricao) {
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
