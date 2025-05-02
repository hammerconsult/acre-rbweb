package br.com.webpublico.entidadesauxiliares.datajud;

import java.io.Serializable;

public class Classe implements Serializable {
    private Integer codigo;
    private String nome;

    public Classe() {
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
