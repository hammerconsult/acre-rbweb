package br.com.webpublico.entidadesauxiliares.comum;

import java.io.Serializable;

public class GrupoRecursoVO implements Serializable {
    private Long id;
    private String nome;
    private String modulo;

    public GrupoRecursoVO() {
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

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }
}
