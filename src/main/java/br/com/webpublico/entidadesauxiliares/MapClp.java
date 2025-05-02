/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;


import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.util.IdentidadeDaEntidade;

import javax.persistence.Transient;

/**
 * @author JaimeDias
 */
public class MapClp {

    private EventoContabil evento;
    private TagValor tagValor;
    @Transient
    private Long criadoEm;

    public MapClp() {
        criadoEm = System.nanoTime();
    }

    public EventoContabil getEvento() {
        return evento;
    }

    public void setEvento(EventoContabil evento) {
        this.evento = evento;
    }

    public TagValor getTagValor() {
        return tagValor;
    }

    public void setTagValor(TagValor tagValor) {
        this.tagValor = tagValor;
    }

    public MapClp(EventoContabil evento, TagValor tagValor) {
        this.evento = evento;
        this.tagValor = tagValor;
        criadoEm = System.nanoTime();
    }

    @Override
    public String toString() {
        return evento + tagValor.getDescricao();
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


}
