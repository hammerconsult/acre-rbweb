package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity

@Audited
public class ParamParcelamentoDivida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParamParcelamento paramParcelamento;
    @ManyToOne
    private Divida divida;
    @ManyToOne
    private Exercicio exercicioInicial;
    @ManyToOne
    private Exercicio exercicioFinal;
    @Transient
    private Long criadoEm;

    public ParamParcelamentoDivida() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public ParamParcelamento getParamParcelamento() {
        return paramParcelamento;
    }

    public void setParamParcelamento(ParamParcelamento paramParcelamento) {
        this.paramParcelamento = paramParcelamento;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ParamParcelamentoDivida[ id=" + id + " ]";
    }
}
