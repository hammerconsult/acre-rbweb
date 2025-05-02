/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Item da Entidade Consignatária")
public class ItemEntidadeConsignataria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Evento Folha de Pagamento")
    @OneToOne
    private EventoFP eventoFP;
    @Tabelavel
    @Etiqueta("Inicio da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Final da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Etiqueta("EventoFP Agrupador")
    @ManyToOne
    private EventoFP eventoFPAgrupador;
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private EntidadeConsignataria entidadeConsignataria;

    public ItemEntidadeConsignataria() {
    }

    public ItemEntidadeConsignataria(EventoFP eventoFP, Date inicioVigencia, Date finalVigencia, EntidadeConsignataria entidadeConsignataria) {
        this.eventoFP = eventoFP;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.entidadeConsignataria = entidadeConsignataria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntidadeConsignataria getEntidadeConsignataria() {
        return entidadeConsignataria;
    }

    public void setEntidadeConsignataria(EntidadeConsignataria entidadeConsignataria) {
        this.entidadeConsignataria = entidadeConsignataria;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    private String getInicioVigenciaFormatado() {
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        if (inicioVigencia != null) {
            return sf.format(inicioVigencia);
        } else {
            return "";
        }
    }

    private String getFinalVigenciaFormatado() {
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        if (finalVigencia != null) {
            return sf.format(finalVigencia);
        } else {
            return "";
        }
    }

    public EventoFP getEventoFPAgrupador() {
        return eventoFPAgrupador;
    }

    public void setEventoFPAgrupador(EventoFP eventoFPAgrupador) {
        this.eventoFPAgrupador = eventoFPAgrupador;
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
        if (!(object instanceof ItemEntidadeConsignataria)) {
            return false;
        }
        ItemEntidadeConsignataria other = (ItemEntidadeConsignataria) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return eventoFP + " - " + getInicioVigenciaFormatado() + " - " + getFinalVigenciaFormatado() + " - " + eventoFPAgrupador;
    }
}
