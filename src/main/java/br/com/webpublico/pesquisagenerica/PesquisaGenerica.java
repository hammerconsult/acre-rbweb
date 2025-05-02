package br.com.webpublico.pesquisagenerica;

import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PesquisaGenerica {
    private Class classe;

    private List<String> atributosTabelaveis;
    private List<String> atributosNaoTabelaveis;
    private List<String> atributosPesquisaveis;
    private List<String> atributosNaoPesquisaveis;
    private List<ItemPesquisaGenerica> itensDePesquisa;

    public PesquisaGenerica() {
        this.atributosTabelaveis = Lists.newArrayList();
        this.atributosNaoTabelaveis = Lists.newArrayList();
        this.atributosPesquisaveis = Lists.newArrayList();
        this.atributosNaoPesquisaveis = Lists.newArrayList();
        itensDePesquisa = Lists.newArrayList();
    }

    public PesquisaGenerica criar(Class classe) {
        this.classe = classe;
        return this;
    }

    public Class getClasse() {
        return classe;
    }

    public void setClasse(Class classe) {
        this.classe = classe;
    }

    public void setAtributosTabelaveis(List<String> atributosTabelaveis) {
        this.atributosTabelaveis = atributosTabelaveis;
    }

    public List<String> getAtributosNaoTabelaveis() {
        return atributosNaoTabelaveis;
    }

    public void setAtributosNaoTabelaveis(List<String> atributosNaoTabelaveis) {
        this.atributosNaoTabelaveis = atributosNaoTabelaveis;
    }

    public void setAtributosPesquisaveis(List<String> atributosPesquisaveis) {
        this.atributosPesquisaveis = atributosPesquisaveis;
    }

    public List<String> getAtributosNaoPesquisaveis() {
        return atributosNaoPesquisaveis;
    }

    public void setAtributosNaoPesquisaveis(List<String> atributosNaoPesquisaveis) {
        this.atributosNaoPesquisaveis = atributosNaoPesquisaveis;
    }

    public List<ItemPesquisaGenerica> getItensDePesquisa() {
        return itensDePesquisa;
    }

    public void setItensDePesquisa(List<ItemPesquisaGenerica> itensDePesquisa) {
        this.itensDePesquisa = itensDePesquisa;
    }

    public PesquisaGenerica addAtributoTabelavel(String... atributos) {
        for (String atributo : atributos) {
            atributosTabelaveis.add(atributo);
        }
        return this;
    }

    public PesquisaGenerica removeAtributoTabelavel(String... atributos) {
        for (String atributo : atributos) {
            atributosNaoTabelaveis.add(atributo);
        }
        return this;
    }

    public PesquisaGenerica addAtributoPesquisavel(String... atributos) {
        for (String atributo : atributos) {
            atributosPesquisaveis.add(atributo);
        }
        return this;
    }

    public PesquisaGenerica removeAtributoPesquisavel(String... atributos) {
        for (String atributo : atributos) {
            atributosNaoPesquisaveis.add(atributo);
        }
        return this;
    }

    public List<Field> getAtributosTabelaveis() {
        List<Field> fieldsTabelaveis = Lists.newArrayList();
        for (Field f : getClasse().getDeclaredFields()) {
            f.setAccessible(true);
            if ((f.getAnnotation(Tabelavel.class) != null || atributosTabelaveis.contains(f.getName())) && !atributosNaoTabelaveis.contains(f.getName())) {
                fieldsTabelaveis.add(f);
            }
        }
        return fieldsTabelaveis;
    }

    public List<Field> getAtributosPesquisaveis() {
        List<Field> fieldsTabelaveis = Lists.newArrayList();
        for (Field f : getClasse().getDeclaredFields()) {
            f.setAccessible(true);
            if ((f.getAnnotation(Pesquisavel.class) != null || atributosPesquisaveis.contains(f.getName())) && !atributosNaoPesquisaveis.contains(f.getName())) {
                fieldsTabelaveis.add(f);
            }
        }
        return fieldsTabelaveis;
    }
}
