package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;


public class ReportRelatoriosDTO implements Serializable{

    private String nome;
    private Integer quantidade;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
