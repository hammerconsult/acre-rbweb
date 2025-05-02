package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by William on 24/01/2019.
 */
public class MemorandoDTO implements Serializable {
    private Long id;
    private String titulo;
    private String conteudo;
    private String resposta;
    private Date dataEnvio;
    private Long usuarioOrigem;
    private String nomeUsuarioOrigem;
    private Long usuarioDestino;
    private Long idPessoaDestino;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public Long getUsuarioOrigem() {
        return usuarioOrigem;
    }

    public void setUsuarioOrigem(Long usuarioOrigem) {
        this.usuarioOrigem = usuarioOrigem;
    }

    public Long getUsuarioDestino() {
        return usuarioDestino;
    }

    public void setUsuarioDestino(Long usuarioDestino) {
        this.usuarioDestino = usuarioDestino;
    }


    public Long getIdPessoaDestino() {
        return idPessoaDestino;
    }

    public void setIdPessoaDestino(Long idPessoaDestino) {
        this.idPessoaDestino = idPessoaDestino;
    }

    public String getNomeUsuarioOrigem() {
        return nomeUsuarioOrigem;
    }

    public void setNomeUsuarioOrigem(String nomeUsuarioOrigem) {
        this.nomeUsuarioOrigem = nomeUsuarioOrigem;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }
}
