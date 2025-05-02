/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author wiplash
 */
@Entity

@Audited
public class EventoReprocessarUO implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EventosReprocessar eventosReprocessar;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    public EventoReprocessarUO(EventosReprocessar eventosReprocessar, UnidadeOrganizacional unidadeOrganizacional) {
        this.eventosReprocessar = eventosReprocessar;
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public EventoReprocessarUO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventosReprocessar getEventosReprocessar() {
        return eventosReprocessar;
    }

    public void setEventosReprocessar(EventosReprocessar eventosReprocessar) {
        this.eventosReprocessar = eventosReprocessar;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EventoReprocessarUO)) {
            return false;
        }
        EventoReprocessarUO other = (EventoReprocessarUO) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.EventoReprocessarUO[ id=" + id + " ]";
    }
}
