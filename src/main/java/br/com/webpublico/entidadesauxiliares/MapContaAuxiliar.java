/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.PlanoDeContas;
import br.com.webpublico.util.IdentidadeDaEntidade;

import javax.persistence.Transient;

/**
 * @author JaimeDias
 */
public class MapContaAuxiliar {

    private PlanoDeContas pdc;
    private String codigo;
    @Transient
    private Long criadoEm;

    public MapContaAuxiliar() {
        criadoEm = System.nanoTime();
    }

    public PlanoDeContas getPdc() {
        return pdc;
    }

    public void setPdc(PlanoDeContas pdc) {
        this.pdc = pdc;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public MapContaAuxiliar(PlanoDeContas pdc, String codigo) {
        this.pdc = pdc;
        this.codigo = codigo;
        criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return pdc + codigo;
    }


}
