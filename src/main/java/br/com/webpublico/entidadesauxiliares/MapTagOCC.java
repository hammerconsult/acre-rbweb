/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.TagOCC;
import br.com.webpublico.util.IdentidadeDaEntidade;

import javax.persistence.Transient;

/**
 * @author JaimeDias
 */
public class MapTagOCC {

    private TagOCC tagOCC;
    private String id;
    @Transient
    private Long criadoEm;

    public MapTagOCC() {
        criadoEm = System.nanoTime();
    }

    public TagOCC getTagOCC() {
        return tagOCC;
    }

    public void setTagOCC(TagOCC tagOCC) {
        this.tagOCC = tagOCC;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MapTagOCC(TagOCC tagOCC, String id) {
        this.tagOCC = tagOCC;
        this.id = id;
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
        return tagOCC + id;
    }


}
