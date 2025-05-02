package br.com.webpublico.nfse.domain.dtos;

import java.io.ByteArrayOutputStream;

public class AnexoEmailDTO {

    private ByteArrayOutputStream anexo;
    private String name;
    private String mimeType;
    private String extension;

    public AnexoEmailDTO(ByteArrayOutputStream anexo, String name, String mimeType, String extension) {
        this.anexo = anexo;
        this.name = name;
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public ByteArrayOutputStream getAnexo() {
        return anexo;
    }

    public void setAnexo(ByteArrayOutputStream anexo) {
        this.anexo = anexo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
