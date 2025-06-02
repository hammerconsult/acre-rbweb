package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Requisição de Compra Execução")
public class RequisicaoCompraExecucao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Requisição de Compra")
    private RequisicaoDeCompra requisicaoCompra;

    @ManyToOne
    @Etiqueta("Execução Contrato Empenho")
    private ExecucaoContratoEmpenho execucaoContratoEmpenho;

    @ManyToOne
    @Etiqueta("Execução Processo Empenho")
    private ExecucaoProcessoEmpenho execucaoProcessoEmpenho;

    @ManyToOne
    @Etiqueta("Execução Reconhecimento Dívida")
    private SolicitacaoEmpenhoReconhecimentoDivida execucaoReconhecimentoDiv;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequisicaoDeCompra getRequisicaoCompra() {
        return requisicaoCompra;
    }

    public void setRequisicaoCompra(RequisicaoDeCompra requisicaoCompra) {
        this.requisicaoCompra = requisicaoCompra;
    }


    public ExecucaoContratoEmpenho getExecucaoContratoEmpenho() {
        return execucaoContratoEmpenho;
    }

    public void setExecucaoContratoEmpenho(ExecucaoContratoEmpenho execucaoContratoEmpenho) {
        this.execucaoContratoEmpenho = execucaoContratoEmpenho;
    }

    public ExecucaoProcessoEmpenho getExecucaoProcessoEmpenho() {
        return execucaoProcessoEmpenho;
    }

    public void setExecucaoProcessoEmpenho(ExecucaoProcessoEmpenho execucaoProcessoEmpenho) {
        this.execucaoProcessoEmpenho = execucaoProcessoEmpenho;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return getExecucaoContratoEmpenho().getExecucaoContrato();
    }

    public ExecucaoProcesso getExecucaoProcesso() {
        return getExecucaoProcessoEmpenho().getExecucaoProcesso();
    }

    public ReconhecimentoDivida getReconhecimentoDivida() {
        return execucaoReconhecimentoDiv.getReconhecimentoDivida();
    }

    public SolicitacaoEmpenhoReconhecimentoDivida getExecucaoReconhecimentoDiv() {
        return execucaoReconhecimentoDiv;
    }

    public void setExecucaoReconhecimentoDiv(SolicitacaoEmpenhoReconhecimentoDivida execucaoReconhecimentoDivida) {
        this.execucaoReconhecimentoDiv = execucaoReconhecimentoDivida;
    }

    public Long getIdExecucao() {
        if (requisicaoCompra.isTipoContrato()) {
            return getExecucaoContrato().getId();
        } else if (requisicaoCompra.isTipoExecucaoProcesso()) {
            return getExecucaoProcesso().getId();
        } else {
            return getReconhecimentoDivida().getId();
        }
    }

    public Empenho getEmpenho() {
        if (execucaoContratoEmpenho != null) {
            return execucaoContratoEmpenho.getEmpenho();
        } else if (execucaoProcessoEmpenho != null) {
            return execucaoProcessoEmpenho.getEmpenho();
        }
        return execucaoReconhecimentoDiv.getSolicitacaoEmpenho().getEmpenho();
    }

    public Contrato getContrato() {
        return getExecucaoContrato().getContrato();
    }

    @Override
    public String toString() {
        try {
            if (requisicaoCompra.isTipoContrato()) {
                return "Nº " + getExecucaoContrato().getNumero() + " - " + DataUtil.getDataFormatada(getExecucaoContrato().getDataLancamento()) + " - " + Util.formataValor(getExecucaoContrato().getValor());
            } else if (requisicaoCompra.isTipoExecucaoProcesso()) {
                return "Nº " + getExecucaoProcesso().getNumero() + " - " + DataUtil.getDataFormatada(getExecucaoProcesso().getDataLancamento()) + " - " + Util.formataValor(getExecucaoProcesso().getValor());
            }
            return "Nº " + getReconhecimentoDivida().getNumero() + " - " + DataUtil.getDataFormatada(getReconhecimentoDivida().getDataReconhecimento());
        } catch (NullPointerException e) {
            return "";
        }
    }
}
