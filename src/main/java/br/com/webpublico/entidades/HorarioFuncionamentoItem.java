/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.DiaSemana;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Terminal-2
 */
@Entity
@Audited
public class HorarioFuncionamentoItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date horarioEntrada;
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date horarioSaida;
    @Enumerated(EnumType.STRING)
    private DiaSemana diaEntrada;
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSaida;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private HorarioFuncionamento horarioFuncionamento;

    public HorarioFuncionamentoItem() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiaSemana getDiaEntrada() {
        return diaEntrada;
    }

    public void setDiaEntrada(DiaSemana diaEntrada) {
        this.diaEntrada = diaEntrada;
    }

    public DiaSemana getDiaSaida() {
        return diaSaida;
    }

    public void setDiaSaida(DiaSemana diaSaida) {
        this.diaSaida = diaSaida;
    }

    public Date getHorarioEntrada() {
        return horarioEntrada;
    }

    public void setHorarioEntrada(Date horarioEntrada) {
        this.horarioEntrada = horarioEntrada;
    }

    public Date getHorarioSaida() {
        return horarioSaida;
    }

    public void setHorarioSaida(Date horarioSaida) {
        this.horarioSaida = horarioSaida;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public HorarioFuncionamento getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(HorarioFuncionamento horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
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
        if (diaEntrada != null && diaSaida != null) {
            if (diaEntrada != diaSaida) {
                return diaEntrada.getDescricao() + " das " + horarioEntrada + " às " + horarioSaida + " (" + diaSaida.getDescricao() + ") ";
            } else {
                return diaEntrada.getDescricao() + " das " + horarioEntrada + " às (" + diaSaida.getDescricao() + ") " + horarioSaida;
            }
        } else {
            return " entrada " + horarioEntrada + " saída " + horarioSaida;
        }
    }
}
