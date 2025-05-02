package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ConfiguracaoTributarioBI extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private Integer debitosGeralExercicioInicialParte1;
    private Integer debitosGeralExercicioFinalParte1;
    private Integer debitosGeralExercicioInicialParte2;
    private Integer debitosGeralExercicioFinalParte2;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDebitosGeralExercicioInicialParte1() {
        return debitosGeralExercicioInicialParte1;
    }

    public void setDebitosGeralExercicioInicialParte1(Integer debitosGeralExercicioInicialParte1) {
        this.debitosGeralExercicioInicialParte1 = debitosGeralExercicioInicialParte1;
    }

    public Integer getDebitosGeralExercicioFinalParte1() {
        return debitosGeralExercicioFinalParte1;
    }

    public void setDebitosGeralExercicioFinalParte1(Integer debitosGeralExercicioFinalParte1) {
        this.debitosGeralExercicioFinalParte1 = debitosGeralExercicioFinalParte1;
    }

    public Integer getDebitosGeralExercicioInicialParte2() {
        return debitosGeralExercicioInicialParte2;
    }

    public void setDebitosGeralExercicioInicialParte2(Integer debitosGeralExercicioInicialParte2) {
        this.debitosGeralExercicioInicialParte2 = debitosGeralExercicioInicialParte2;
    }

    public Integer getDebitosGeralExercicioFinalParte2() {
        return debitosGeralExercicioFinalParte2;
    }

    public void setDebitosGeralExercicioFinalParte2(Integer debitosGeralExercicioFinalParte2) {
        this.debitosGeralExercicioFinalParte2 = debitosGeralExercicioFinalParte2;
    }
}
