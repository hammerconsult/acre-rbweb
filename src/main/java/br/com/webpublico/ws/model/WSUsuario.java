package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.comum.UsuarioWeb;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: scarpini
 * Date: 28/03/14
 * Time: 16:02
 * To change this template use File | Settings | File Templates.
 */
public class WSUsuario implements Serializable {

    private String senha;
    private String email;
    private Boolean primeiroLogin;
    private Boolean ativo;

    public WSUsuario() {

    }

    public WSUsuario(UsuarioWeb usu) {
        this.senha = usu.getPassword();
        this.email = usu.getEmail();
        this.primeiroLogin = usu.getUltimoAcesso() == null;
        this.ativo = usu.getActivated();
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getPrimeiroLogin() {
        return primeiroLogin;
    }

    public void setPrimeiroLogin(Boolean primeiroLogin) {
        this.primeiroLogin = primeiroLogin;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
