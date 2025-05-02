package br.com.webpublico.pesquisagenerica;

import java.io.Serializable;

public class ItemPesquisaGenerica implements Serializable {
    private String condicao;
    private String label;
    private transient Object tipo;
    private transient Object valor;

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getTipo() {
        return tipo;
    }

    public void setTipo(Object tipo) {
        this.tipo = tipo;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }
}
