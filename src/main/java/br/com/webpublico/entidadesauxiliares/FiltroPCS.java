/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CategoriaPCS;
import br.com.webpublico.entidades.EnquadramentoPCS;
import br.com.webpublico.entidades.ProgressaoPCS;
import br.com.webpublico.entidades.SuperEntidade;

import java.io.Serializable;

/**
 * @author mga
 */
public class FiltroPCS extends SuperEntidade implements Serializable{

    private CategoriaPCS categoriaPCS;
    private ProgressaoPCS progressaoPCS;
    private EnquadramentoPCS enquadramentoPCS;

    public FiltroPCS() {
        super();
    }

    public CategoriaPCS getCategoriaPCS() {
        return categoriaPCS;
    }

    public FiltroPCS(CategoriaPCS categoriaPCS, ProgressaoPCS progressaoPCS, EnquadramentoPCS enquadramentoPCS) {
        super();
        this.categoriaPCS = categoriaPCS;
        this.progressaoPCS = progressaoPCS;
        this.enquadramentoPCS = enquadramentoPCS;
    }

    public void setCategoriaPCS(CategoriaPCS categoriaPCS) {
        this.categoriaPCS = categoriaPCS;
    }

    public ProgressaoPCS getProgressaoPCS() {
        return progressaoPCS;
    }

    public void setProgressaoPCS(ProgressaoPCS progressaoPCS) {
        this.progressaoPCS = progressaoPCS;
    }

    public EnquadramentoPCS getEnquadramentoPCS() {
        return enquadramentoPCS;
    }

    public void setEnquadramentoPCS(EnquadramentoPCS enquadramentoPCS) {
        this.enquadramentoPCS = enquadramentoPCS;
    }

    @Override
    public String toString() {
        return "FiltroPCS{" + '}';
    }

    @Override
    public Long getId() {
        return null;
    }
}
