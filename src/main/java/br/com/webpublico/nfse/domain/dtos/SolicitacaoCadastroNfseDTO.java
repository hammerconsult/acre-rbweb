package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;

/**
 * Created by William on 16/12/2016.
 */
public class SolicitacaoCadastroNfseDTO implements Serializable {

    private String nome;
    private String email;
    private String usuario;
    private PrestadorServicoNfseDTO prestadorServico;
    private String cpf;

    public SolicitacaoCadastroNfseDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public PrestadorServicoNfseDTO getPrestadorServico() {
        return prestadorServico;
    }

    public void setPrestadorServico(PrestadorServicoNfseDTO prestadorServico) {
        this.prestadorServico = prestadorServico;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
