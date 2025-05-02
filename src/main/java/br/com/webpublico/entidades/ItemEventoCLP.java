/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TagValor;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author major
 */
@Entity

@Audited
public class ItemEventoCLP extends SuperEntidade implements ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta(value = "Tag Valor")
    private TagValor tagValor;
    @ManyToOne
    @Tabelavel
    private CLP clp;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataVigencia;
    private Boolean reprocessar;
    @ManyToOne
    private EventoContabil eventoContabil;

    public ItemEventoCLP() {
        super();
        reprocessar = Boolean.FALSE;
    }

    public ItemEventoCLP(TagValor tagValor, CLP clp, Date dataVigencia, Boolean reprocessar, EventoContabil eventoContabil, Long criadoEm) {
        this.tagValor = tagValor;
        this.clp = clp;
        this.dataVigencia = dataVigencia;
        this.reprocessar = reprocessar;
        this.eventoContabil = eventoContabil;
        this.criadoEm = criadoEm;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CLP getClp() {
        return clp;
    }

    public void setClp(CLP clp) {
        this.clp = clp;
    }

    public TagValor getTagValor() {
        return tagValor;
    }

    public void setTagValor(TagValor tagValor) {
        this.tagValor = tagValor;
    }

    public Date getDataVigencia() {
        return dataVigencia;
    }

    public void setDataVigencia(Date dataVigencia) {
        this.dataVigencia = dataVigencia;
    }

    public Boolean getReprocessar() {
        return reprocessar;
    }

    public void setReprocessar(Boolean reprocessar) {
        this.reprocessar = reprocessar;
    }

    @Override
    public String toString() {
        return clp.toString();
    }

    @Override
    public Date getInicioVigencia() {
        return clp.getInicioVigencia();
    }

    @Override
    public Date getFimVigencia() {
        return clp.getFimVigencia();
    }

    @Override
    public void setInicioVigencia(Date data) {
        clp.setInicioVigencia(data);
    }

    @Override
    public void setFimVigencia(Date data) {
        clp.setFimVigencia(data);
    }
}
