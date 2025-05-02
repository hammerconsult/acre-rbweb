package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.Arquivo;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

public class NfseArquivo implements Serializable {

    private Long id;
    private String conteudo;
    private String descricao;
    private String nome;
    private String mimeType;
    private Long tamanho;
    private InputStream inputStream;

    public NfseArquivo() {
        super();
    }

    public NfseArquivo(Arquivo arquivo) {
        this.id = arquivo.getId();
        this.descricao = arquivo.getDescricao();
        this.nome = arquivo.getNome();
        this.mimeType = arquivo.getMimeType();
        this.tamanho = arquivo.getTamanho();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
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

    public InputStream getInputStream() {
        return inputStream;
    }

    public void processarInpuStream() {
        String dataInfo = this.conteudo.split(";base64,")[0];
        String data = this.conteudo.split("base64,")[1];
        try {

            this.mimeType = dataInfo.split(":")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            this.mimeType = null;
        }

        Base64 decoder = new Base64();
        byte[] imgBytes = decoder.decode(data);

        this.tamanho = new Long(imgBytes.length);
        this.inputStream = new ByteArrayInputStream(imgBytes);
    }
}
