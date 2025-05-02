/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Planejamento Estratégico")
@Table(name = "ExercPlanoEstrategico")
/**
 * Vincula os 4 (validado em código) possíveis exercícios fiscais ao planejamento estratégico
 *
 */
public class ExercicioPlanoEstrategico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PlanejamentoEstrategico planejamentoEstrategico;
    @ManyToOne
    private Exercicio exercicio;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public ExercicioPlanoEstrategico() {
        dataRegistro = new Date();
    }

    public ExercicioPlanoEstrategico(PlanejamentoEstrategico planejamentoEstrategico, Exercicio exercicio) {
        this.planejamentoEstrategico = planejamentoEstrategico;
        this.exercicio = exercicio;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanejamentoEstrategico getPlanejamentoEstrategico() {
        return planejamentoEstrategico;
    }

    public void setPlanejamentoEstrategico(PlanejamentoEstrategico planejamentoEstrategico) {
        this.planejamentoEstrategico = planejamentoEstrategico;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExercicioPlanoEstrategico other = (ExercicioPlanoEstrategico) obj;
        if (this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return exercicio.getAno() + "";
    }
}
