package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * Created by William on 11/01/2016.
 */

@Entity
@Audited
public class PessoaCNAE extends SuperEntidade implements Comparable<PessoaCNAE> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CNAE cnae;
    @ManyToOne
    private Pessoa pessoa;
    @Temporal(TemporalType.DATE)
    private Date inicio;
    @Temporal(TemporalType.DATE)
    private Date fim;
    @Enumerated(EnumType.STRING)
    private EconomicoCNAE.TipoCnae tipo;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataregistro;
    @ManyToOne
    private HorarioFuncionamento horarioFuncionamento;
    private Boolean exercidaNoLocal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public EconomicoCNAE.TipoCnae getTipo() {
        return tipo;
    }

    public void setTipo(EconomicoCNAE.TipoCnae tipo) {
        this.tipo = tipo;
    }

    public Date getDataregistro() {
        return dataregistro;
    }

    public void setDataregistro(Date dataregistro) {
        this.dataregistro = dataregistro;
    }

    public HorarioFuncionamento getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(HorarioFuncionamento horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public Boolean getExercidaNoLocal() {
        return exercidaNoLocal != null ? exercidaNoLocal : Boolean.FALSE;
    }

    public void setExercidaNoLocal(Boolean exercidaNoLocal) {
        this.exercidaNoLocal = exercidaNoLocal;
    }

    @Override
    public int compareTo(PessoaCNAE o) {
        int i = this.getCnae() != null && o.getCnae() != null ?
            this.getCnae().getSituacao().compareTo(o.getCnae().getSituacao()) : 0;
        if (i == 0) {
            i = this.getTipo() != null && o.getTipo() != null ?
                this.getTipo().compareTo(o.getTipo()) : 0;
        }
        if (i == 0) {
            i = this.getCnae() != null && o.getCnae() != null ?
                this.getCnae().getCodigoCnae().compareTo(o.getCnae().getCodigoCnae()) : 0;
        }
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PessoaCNAE that = (PessoaCNAE) o;
        return Objects.equals(cnae, that.cnae) &&
            tipo == that.tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cnae, tipo);
    }
}
