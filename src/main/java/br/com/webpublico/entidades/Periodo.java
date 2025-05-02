/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.usertype.DiaDaSemanaUserType;
import br.com.webpublico.entidades.usertype.LocalTimeUserType;
import br.com.webpublico.enums.DiaSemana;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Interval;
import org.joda.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "Segurança")
@Entity

@Audited
@Etiqueta("Período de Login")
@TypeDefs({
    @TypeDef(name = "diaDaSemana", typeClass = DiaDaSemanaUserType.class),
    @TypeDef(name = "localTime", typeClass = LocalTimeUserType.class, defaultForType = LocalTime.class)})
public class Periodo extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    @Type(type = "diaDaSemana")
    private DiaSemana diaDaSemana;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date inicio;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date termino;

    public Periodo() {
    }

    public Periodo(DiaSemana diaDaSemana, Date inicio, Date termino) {
        this.diaDaSemana = diaDaSemana;
        this.inicio = inicio;
        this.termino = termino;
    }

    public boolean isNoPeriodo(DateTime dateTime) {
        try {
            DateTime inicioDateTime = new DateTime(inicio);
            DateTime terminoDateTime = new DateTime(termino);
            DateTime dataInicio = diaDaSemana.getDayOfWeek().with(DateTimeFieldType.hourOfDay(), inicioDateTime.getHourOfDay()).with(DateTimeFieldType.minuteOfHour(), inicioDateTime.getMinuteOfHour()).toDateTime(dateTime);
            DateTime dataTermino = diaDaSemana.getDayOfWeek().with(DateTimeFieldType.hourOfDay(), terminoDateTime.getHourOfDay()).with(DateTimeFieldType.minuteOfHour(), terminoDateTime.getMinuteOfHour()).toDateTime(dateTime);
            return new Interval(dataInicio, dataTermino.plus(1)).contains(dateTime);
        } catch (Exception e) {
            return false;
        }
    }

    public DiaSemana getDiaDaSemana() {
        return diaDaSemana;
    }

    public void setDiaDaSemana(DiaSemana diaDaSemana) {
        this.diaDaSemana = diaDaSemana;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getTermino() {
        return termino;
    }

    public void setTermino(Date termino) {
        this.termino = termino;
    }
}
