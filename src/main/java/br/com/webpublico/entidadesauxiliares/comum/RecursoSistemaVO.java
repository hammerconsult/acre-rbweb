package br.com.webpublico.entidadesauxiliares.comum;

import br.com.webpublico.enums.TipoPessoa;

import java.io.Serializable;
import java.util.Date;

public class RecursoSistemaVO implements Serializable {
    private Long id;
    private String nome;
    private String caminho;

    public RecursoSistemaVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
