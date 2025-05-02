package br.com.webpublico.entidades.comum.consultasql;

import java.math.BigDecimal;

/**
 * Created by renatoromanini on 01/09/15.
 */
public class ColunaView {

    private Object valor;
    private String nomeColuna;
    private Class classe;
    private Boolean entidade;
    private Object objeto;
    private Boolean titulo;

    public ColunaView(Object valor, String nomeColuna, Class classe, Boolean entidade, Object objeto) {
        this.valor = valor;
        this.nomeColuna = nomeColuna;
        this.classe = classe;
        this.entidade = entidade;
        this.objeto = objeto;
        this.titulo = Boolean.FALSE;
    }

    public Object getObjeto() {
        return objeto;
    }

    public void setObjeto(Object objeto) {
        this.objeto = objeto;
    }

    public Class getClasse() {
        return classe;
    }

    public void setClasse(Class classe) {
        this.classe = classe;
    }

    public Boolean getEntidade() {
        return entidade;
    }

    public void setEntidade(Boolean entidade) {
        this.entidade = entidade;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public String getNomeColuna() {
        return nomeColuna;
    }

    public void setNomeColuna(String nomeColuna) {
        this.nomeColuna = nomeColuna;
    }

    public Boolean getTitulo() {
        return titulo;
    }

    public void setTitulo(Boolean titulo) {
        this.titulo = titulo;
    }

    @Override
    public String toString() {
        if (valor != null) {
            return valor.toString();
        } else {
            return nomeColuna;
        }
    }

    public Object getValorParaExportar() {
        if (this.isCampoValor()) {
            return this.getObjeto();
        } else {
            return this.getValor();
        }
    }

    public Boolean isCampoValor() {
        return classe.equals(BigDecimal.class) && !nomeColuna.toLowerCase().contains("id");
    }
}
