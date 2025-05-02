package br.com.webpublico.entidadesauxiliares.comum;

import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.ws.model.WSPessoa;

import java.util.Date;

public class UsuarioPortalWebDTO {

    private WSPessoa pessoa;
    private String nome;
    private String cpf;
    private String email;
    private String nomeMae;
    private Date nascimento;
    private String mensagem;


    public UsuarioPortalWebDTO() {
    }

    public UsuarioPortalWebDTO(UsuarioWeb usuarioWeb, WSPessoa wsPessoa) {
        this.nome = usuarioWeb.getCreatedBy();
        this.cpf = usuarioWeb.getLogin();
        this.email = usuarioWeb.getEmail();
        this.pessoa = wsPessoa;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public Date getNascimento() {
        return nascimento;
    }

    public void setNascimento(Date nascimento) {
        this.nascimento = nascimento;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public WSPessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(WSPessoa pessoa) {
        this.pessoa = pessoa;
    }
}
