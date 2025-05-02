package br.com.webpublico.pncp.dto;

public class ContratacaoResponseDTO {

    private String documentoUri;
    private String compraUri;

    public String getDocumentoUri() {
        return documentoUri;
    }

    public void setDocumentoUri(String documentoUri) {
        this.documentoUri = documentoUri;
    }

    public String getCompraUri() {
        return compraUri;
    }

    public void setCompraUri(String compraUri) {
        this.compraUri = compraUri;
    }
}
