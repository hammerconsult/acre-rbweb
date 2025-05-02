package br.com.webpublico.nfse.domain.dtos;

import java.util.List;

public class ArquivoNfseDTO implements NfseDTO {

    private Long id;
    private String descricao;
    private String nome;
    private String mimeType;
    private Long tamanho;
    private List<ArquivoParteNfseDTO> partes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

    public List<ArquivoParteNfseDTO> getPartes() {
        return partes;
    }

    public void setPartes(List<ArquivoParteNfseDTO> partes) {
        this.partes = partes;
    }
}
