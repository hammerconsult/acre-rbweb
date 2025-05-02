package br.com.webpublico.ws.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WSDocumentoPortalServidor implements Serializable {

    private Long id;
    private String nomeNoPortal;
    private String nomeDoArquivo;
    private Date publicadoEm;
    private Date atualizadoEm;
    private String mimeType;
    private byte[] dados;
    private Integer ordem;
    private Long superior;
    private List<WSDocumentoPortalServidor> filhos;

    public WSDocumentoPortalServidor() {
        ordem = 1;
        filhos = new ArrayList<>();
    }

    public String getNomeNoPortal() {
        return nomeNoPortal;
    }

    public void setNomeNoPortal(String nomeNoPortal) {
        this.nomeNoPortal = nomeNoPortal;
    }

    public String getNomeDoArquivo() {
        return nomeDoArquivo;
    }

    public void setNomeDoArquivo(String nomeDoArquivo) {
        this.nomeDoArquivo = nomeDoArquivo;
    }

    public Date getPublicadoEm() {
        return publicadoEm;
    }

    public void setPublicadoEm(Date publicadoEm) {
        this.publicadoEm = publicadoEm;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getDados() {
        return dados;
    }

    public void setDados(byte[] dados) {
        this.dados = dados;
    }

    public Date getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Date atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSuperior() {
        return superior;
    }

    public void setSuperior(Long superior) {
        this.superior = superior;
    }

    public List<WSDocumentoPortalServidor> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<WSDocumentoPortalServidor> filhos) {
        this.filhos = filhos;
    }
}
