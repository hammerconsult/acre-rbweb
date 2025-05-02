package br.com.webpublico.util;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Daniel
 */
public class CampoDB implements Serializable {
    private String tabela;
    private String nome;
    private String descricao;
    private Object valor;

    public CampoDB(String tabela, String nome, String descricao, Object valor) {
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.tabela = tabela;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return tabela + "." + nome + " (" + descricao + "): " + valor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CampoDB other = (CampoDB) obj;
        if (!Objects.equals(this.tabela, other.tabela)) {
            return false;
        }
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.tabela);
        hash = 17 * hash + Objects.hashCode(this.nome);
        return hash;
    }

}
