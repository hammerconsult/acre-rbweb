/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusEventoReprocessar;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wiplash
 *         <p/>
 *         Essa entidade guardará os registros dos eventos contabeis alterados nas telas
 *         onde ele aparece, para que os mesmos sejam reprocessados contabilmente por
 *         ex: EventoContabil, CLPCOnfiguração, itemEventoCLP, OrigemOCC, TAGOCC, etc...
 */
@Entity

@Audited
@Etiqueta("Eventos Contábeis a Reprocessar")
public class EventosReprocessar implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataInicial;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataFinal;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataRegistro;
    @ManyToOne
    private EventoContabil eventoContabil;
    private String classeOrigem;
    private Long idOrigem;
    @Enumerated(EnumType.STRING)
    private StatusEventoReprocessar statusEventoReprocessar;
    @OneToMany(mappedBy = "eventosReprocessar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoReprocessarUO> eventoReprocessarUOs;
    @Transient
    private Long criadoEm;
    @Transient
    private List<Object> objetosParaReprocessar;

    public EventosReprocessar() {
        statusEventoReprocessar = StatusEventoReprocessar.PENDENTE;
        dataRegistro = new Date();
        eventoReprocessarUOs = new ArrayList<>();
        this.criadoEm = System.nanoTime();
        objetosParaReprocessar = new ArrayList<>();
    }

    public EventosReprocessar(Date dataInicial, Date dataFinal, Date dataRegistro, EventoContabil eventoContabil, String classeOrigem, Long idOrigem, StatusEventoReprocessar statusEventoReprocessar, List<EventoReprocessarUO> eventoReprocessarUOs) {
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.dataRegistro = dataRegistro;
        this.eventoContabil = eventoContabil;
        this.classeOrigem = classeOrigem;
        this.idOrigem = idOrigem;
        this.statusEventoReprocessar = statusEventoReprocessar;
        this.eventoReprocessarUOs = eventoReprocessarUOs;
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public String getClasseOrigem() {
        return classeOrigem;
    }

    public void setClasseOrigem(String classeOrigem) {
        this.classeOrigem = classeOrigem;
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public StatusEventoReprocessar getStatusEventoReprocessar() {
        return statusEventoReprocessar;
    }

    public void setStatusEventoReprocessar(StatusEventoReprocessar statusEventoReprocessar) {
        this.statusEventoReprocessar = statusEventoReprocessar;
    }

    public List<EventoReprocessarUO> getEventoReprocessarUOs() {
        return eventoReprocessarUOs;
    }

    public void setEventoReprocessarUOs(List<EventoReprocessarUO> eventoReprocessarUOs) {
        this.eventoReprocessarUOs = eventoReprocessarUOs;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<Object> getObjetosParaReprocessar() {
        return objetosParaReprocessar;
    }

    public void setObjetosParaReprocessar(List<Object> objetosParaReprocessar) {
        this.objetosParaReprocessar = objetosParaReprocessar;
    }


    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "Evento: " + this.getEventoContabil() + " ( " + DataUtil.getDataFormatada(dataInicial) + " - " + DataUtil.getDataFormatada(dataFinal) + " )";
    }

    public List<UnidadeOrganizacional> getUnidades() {
        List<UnidadeOrganizacional> retorno = new ArrayList<UnidadeOrganizacional>();
        for (EventoReprocessarUO eventoReprocessarUO : getEventoReprocessarUOs()) {
            retorno.add(eventoReprocessarUO.getUnidadeOrganizacional());
        }
        return retorno;
    }

    public Boolean hasUnidades() {
        return (this.getUnidades() != null && !this.getUnidades().isEmpty());
    }

    public List<Long> getIdUnidades() {
        List<Long> retorno = Lists.newArrayList();
        for (EventoReprocessarUO eventoReprocessarUO : getEventoReprocessarUOs()) {
            retorno.add(eventoReprocessarUO.getUnidadeOrganizacional().getId());
        }
        return retorno;
    }


    public TipoEventoContabil getTipoEventoContabil() {
        return this.getEventoContabil().getTipoEventoContabil();
    }
}
