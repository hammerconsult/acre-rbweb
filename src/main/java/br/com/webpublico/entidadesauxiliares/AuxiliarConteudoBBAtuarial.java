/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.SuperEntidade;

import java.io.Serializable;

/**
 * @author Felipe Marinzeck
 */
public class AuxiliarConteudoBBAtuarial extends SuperEntidade implements Serializable{
    private String codigo;
    private String descricao;

    public AuxiliarConteudoBBAtuarial() {
        this.criadoEm = System.nanoTime();
    }

    @Override
    public Long getId() {
        return null;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
