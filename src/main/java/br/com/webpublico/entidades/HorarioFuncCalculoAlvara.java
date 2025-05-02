package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class HorarioFuncCalculoAlvara extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoCalculoAlvara processoCalculoAlvara;
    @ManyToOne
    private HorarioFuncionamento horarioFuncionamento;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoCalculoAlvara getProcessoCalculoAlvara() {
        return processoCalculoAlvara;
    }

    public void setProcessoCalculoAlvara(ProcessoCalculoAlvara processoCalculoAlvara) {
        this.processoCalculoAlvara = processoCalculoAlvara;
    }

    public HorarioFuncionamento getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(HorarioFuncionamento horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }
}
