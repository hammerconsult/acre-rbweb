package br.com.webpublico.negocios;

import org.primefaces.model.UploadedFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class UploadArquivo implements UploadedFile {
    private String nomeArquivo;
    private String mimeType;
    private InputStream input;
    private long tamanho;
    private byte[] bytes;

    public UploadArquivo(String nomeArquivo, String mimeType, byte[] bytes) {
        this.nomeArquivo = nomeArquivo;
        this.mimeType = mimeType;
        this.input = new ByteArrayInputStream(bytes != null ? bytes : new byte[0]);
        this.tamanho = bytes != null ? bytes.length : 0;
        this.bytes = bytes;
    }

    @Override
    public String getFileName() {
        return nomeArquivo;
    }

    public void setFileName(String fileName) {
        this.nomeArquivo = fileName;
    }

    @Override
    public String getContentType() {
        return mimeType;
    }

    public void setContentType(String contentType) {
        this.mimeType = contentType;
    }

    @Override
    public InputStream getInputstream() {
        return input;
    }

    public void setInputstream(InputStream inputstream) {
        this.input = inputstream;
    }

    @Override
    public long getSize() {
        return tamanho;
    }

    public void setSize(long size) {
        this.tamanho = size;
    }

    @Override
    public byte[] getContents() {
        return bytes;
    }

    public void setContents(byte[] contents) {
        this.bytes = contents;
    }
}
