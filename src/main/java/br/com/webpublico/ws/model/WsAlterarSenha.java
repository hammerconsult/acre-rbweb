package br.com.webpublico.ws.model;

import java.io.Serializable;

public class WsAlterarSenha implements Serializable {

    protected String cpf;
    protected String senha;

    public WsAlterarSenha(String cpf, String senha) {
        this.cpf = cpf;
        this.senha = senha;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String value) {
        this.senha = value;
    }

}
