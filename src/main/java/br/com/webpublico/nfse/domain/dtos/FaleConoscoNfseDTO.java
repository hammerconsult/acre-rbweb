package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.SituacaoFaleConoscoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.TipoFaleConoscoNfseDTO;

import java.util.Date;

public class FaleConoscoNfseDTO implements NfseDTO {

    private Long id;
    private TipoFaleConoscoNfseDTO tipo;
    private String assunto;
    private Date dataEnvio;
    private String nome;
    private String mensagem;
    private String email;
    private String telefone;
    private SituacaoFaleConoscoNfseDTO situacao;

    public FaleConoscoNfseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoFaleConoscoNfseDTO getTipo() {
        return tipo;
    }

    public void setTipo(TipoFaleConoscoNfseDTO tipo) {
        this.tipo = tipo;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public SituacaoFaleConoscoNfseDTO getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoFaleConoscoNfseDTO situacao) {
        this.situacao = situacao;
    }
}
