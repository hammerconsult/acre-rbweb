package br.com.webpublico.pncp.dto;

public class DocumentoDTO {
    private String sequencial;
    private String tituloDocumento;
    private String tipoDocumentoId;

    public String getSequencial() {
        return sequencial;
    }

    public void setSequencial(String sequencial) {
        this.sequencial = sequencial;
    }

    public String getTituloDocumento() {
        return tituloDocumento;
    }

    public void setTituloDocumento(String tituloDocumento) {
        this.tituloDocumento = tituloDocumento;
    }

    public String getTipoDocumentoId() {
        return tipoDocumentoId;
    }

    public void setTipoDocumentoId(String tipoDocumentoId) {
        this.tipoDocumentoId = tipoDocumentoId;
    }
}
