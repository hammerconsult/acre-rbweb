package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class HorariosFuncAlteracao extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private HorarioFuncionamento horarioFuncionamento;
    @ManyToOne
    private AlteracaoHorFuncionamento alteracaoHorFuncionamento;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HorarioFuncionamento getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(HorarioFuncionamento horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public AlteracaoHorFuncionamento getAlteracaoHorFuncionamento() {
        return alteracaoHorFuncionamento;
    }

    public void setAlteracaoHorFuncionamento(AlteracaoHorFuncionamento alteracaoHorFuncionamento) {
        this.alteracaoHorFuncionamento = alteracaoHorFuncionamento;
    }
}
