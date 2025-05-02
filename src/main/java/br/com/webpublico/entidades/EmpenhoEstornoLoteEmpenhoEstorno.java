package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "EMPENHOESTORNOLOTEEMPEST")
public class EmpenhoEstornoLoteEmpenhoEstorno extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EmpenhoEstornoLote empenhoEstornoLote;
    @ManyToOne
    private EmpenhoEstorno empenhoEstorno;
    @ManyToOne
    private SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno;
    @Transient
    private ExecucaoContratoEstorno execucaoContratoEstorno;
    @Transient
    private ExecucaoProcessoEstorno execucaoProcessoEstorno;

    public EmpenhoEstornoLoteEmpenhoEstorno() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public EmpenhoEstornoLote getEmpenhoEstornoLote() {
        return empenhoEstornoLote;
    }

    public void setEmpenhoEstornoLote(EmpenhoEstornoLote empenhoEstornoLote) {
        this.empenhoEstornoLote = empenhoEstornoLote;
    }

    public EmpenhoEstorno getEmpenhoEstorno() {
        return empenhoEstorno;
    }

    public void setEmpenhoEstorno(EmpenhoEstorno empenhoEstorno) {
        this.empenhoEstorno = empenhoEstorno;
    }

    public SolicitacaoEmpenhoEstorno getSolicitacaoEmpenhoEstorno() {
        return solicitacaoEmpenhoEstorno;
    }

    public void setSolicitacaoEmpenhoEstorno(SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno) {
        this.solicitacaoEmpenhoEstorno = solicitacaoEmpenhoEstorno;
    }

    public ExecucaoContratoEstorno getExecucaoContratoEstorno() {
        return execucaoContratoEstorno;
    }

    public void setExecucaoContratoEstorno(ExecucaoContratoEstorno execucaoContratoEstorno) {
        this.execucaoContratoEstorno = execucaoContratoEstorno;
    }

    public ExecucaoProcessoEstorno getExecucaoProcessoEstorno() {
        return execucaoProcessoEstorno;
    }

    public void setExecucaoProcessoEstorno(ExecucaoProcessoEstorno execucaoProcessoEstorno) {
        this.execucaoProcessoEstorno = execucaoProcessoEstorno;
    }
}
