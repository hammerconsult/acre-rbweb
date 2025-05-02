package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("CNAE Processo Cálculo Alvará")
@GrupoDiagrama(nome = "Alvara")
public class CNAEProcessoCalculoAlvara extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CNAE cnae;
    @Enumerated(EnumType.STRING)
    private EconomicoCNAE.TipoCnae tipoCnae;
    @ManyToOne
    private ProcessoCalculoAlvara processoCalculoAlvara;
    @ManyToOne
    private HorarioFuncionamento horarioFuncionamento;
    private Boolean exercidaNoLocal;
    @Transient
    private Date inicioAtividade;

    public CNAEProcessoCalculoAlvara() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EconomicoCNAE.TipoCnae getTipoCnae() {
        return tipoCnae;
    }

    public void setTipoCnae(EconomicoCNAE.TipoCnae tipoCnae) {
        this.tipoCnae = tipoCnae;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public HorarioFuncionamento getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(HorarioFuncionamento horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public ProcessoCalculoAlvara getProcessoCalculoAlvara() {
        return processoCalculoAlvara;
    }

    public void setProcessoCalculoAlvara(ProcessoCalculoAlvara processoCalculoAlvara) {
        this.processoCalculoAlvara = processoCalculoAlvara;
    }

    public Boolean getExercidaNoLocal() {
        return exercidaNoLocal != null ? exercidaNoLocal : Boolean.FALSE;
    }

    public void setExercidaNoLocal(Boolean exercidaNoLocal) {
        this.exercidaNoLocal = exercidaNoLocal;
    }

    public Date getInicioAtividade() {
        return inicioAtividade;
    }

    public void setInicioAtividade(Date inicioAtividade) {
        this.inicioAtividade = inicioAtividade;
    }
}
