package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ProcessoRevisaoCalculoIPTUExercicio extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU;
    @ManyToOne
    private Exercicio exercicio;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoRevisaoCalculoIPTU getProcessoRevisaoCalculoIPTU() {
        return processoRevisaoCalculoIPTU;
    }

    public void setProcessoRevisaoCalculoIPTU(ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU) {
        this.processoRevisaoCalculoIPTU = processoRevisaoCalculoIPTU;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }
}
