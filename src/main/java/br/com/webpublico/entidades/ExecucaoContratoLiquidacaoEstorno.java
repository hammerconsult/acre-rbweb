package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@Entity
@Etiqueta("Solicitação Estorno Execução Liquidação")
@Table(name = "EXECUCAOCONTRATOLIQUIDEST")
public class ExecucaoContratoLiquidacaoEstorno extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ExecucaoContratoEmpenhoEstorno execucaoContratoEmpenhoEst;

    @ManyToOne(cascade = CascadeType.ALL)
    private SolicitacaoLiquidacaoEstorno solicitacaoLiquidacaoEst;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoContratoEmpenhoEstorno getExecucaoContratoEmpenhoEst() {
        return execucaoContratoEmpenhoEst;
    }

    public void setExecucaoContratoEmpenhoEst(ExecucaoContratoEmpenhoEstorno execucaoContratoEmpenhoEst) {
        this.execucaoContratoEmpenhoEst = execucaoContratoEmpenhoEst;
    }

    public SolicitacaoLiquidacaoEstorno getSolicitacaoLiquidacaoEst() {
        return solicitacaoLiquidacaoEst;
    }

    public void setSolicitacaoLiquidacaoEst(SolicitacaoLiquidacaoEstorno solicitacaoLiquidacaoEst) {
        this.solicitacaoLiquidacaoEst = solicitacaoLiquidacaoEst;
    }
}
