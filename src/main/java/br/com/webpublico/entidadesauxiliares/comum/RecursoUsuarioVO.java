package br.com.webpublico.entidadesauxiliares.comum;

import java.io.Serializable;

public class RecursoUsuarioVO implements Serializable {
    private String nome;
    private String caminho;

    public RecursoUsuarioVO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }
}
