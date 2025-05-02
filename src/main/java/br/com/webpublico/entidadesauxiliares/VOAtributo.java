package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class VOAtributo implements Serializable {

    private Long id;
    private Long codigo;
    private String valor;

    public VOAtributo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        String descricao = "";
        if(codigo != null) {
            descricao += codigo;
            if(valor != null) {
                descricao += " - ";
            }
        }
        if(valor != null) {
            descricao += valor;
        }
        return descricao;
    }
}
