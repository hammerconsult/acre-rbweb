package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DocumentoFiscalIntegracaoAssistente implements Serializable {

    private DoctoFiscalLiquidacao doctoFiscalLiquidacao;
    private Empenho empenho;
    private Desdobramento desdobramento;
    private DesdobramentoLiquidacaoEstorno desdobramentoEstorno;
    private boolean lancamentoNormal;
    private boolean dadosComponente;
    private boolean comSaldo;
    private boolean integrado;
    private BigDecimal valorEstornarDocumento;
    private Date data;
    private Exercicio exercicio;
    private UnidadeOrganizacional unidadeOrganizacional;

    public DocumentoFiscalIntegracaoAssistente() {
        lancamentoNormal = true;
        dadosComponente = false;
        comSaldo = false;
        integrado = false;
    }

    public boolean getLancamentoNormal() {
        return lancamentoNormal;
    }

    public void setLancamentoNormal(boolean lancamentoNormal) {
        this.lancamentoNormal = lancamentoNormal;
    }

    public boolean getDadosComponente() {
        return dadosComponente;
    }

    public void setDadosComponente(boolean dadosComponente) {
        this.dadosComponente = dadosComponente;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public Desdobramento getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(Desdobramento desdobramento) {
        this.desdobramento = desdobramento;
    }

    public DesdobramentoLiquidacaoEstorno getDesdobramentoEstorno() {
        return desdobramentoEstorno;
    }

    public void setDesdobramentoEstorno(DesdobramentoLiquidacaoEstorno desdobramentoEstorno) {
        this.desdobramentoEstorno = desdobramentoEstorno;
    }

    public DoctoFiscalLiquidacao getDoctoFiscalLiquidacao() {
        return doctoFiscalLiquidacao;
    }

    public void setDoctoFiscalLiquidacao(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        this.doctoFiscalLiquidacao = doctoFiscalLiquidacao;
    }

    public BigDecimal getValorEstornarDocumento() {
        return valorEstornarDocumento;
    }

    public void setValorEstornarDocumento(BigDecimal valorEstornarDocumento) {
        this.valorEstornarDocumento = valorEstornarDocumento;
    }

    public boolean getComSaldo() {
        return comSaldo;
    }

    public void setComSaldo(boolean comSaldo) {
        this.comSaldo = comSaldo;
    }

    public boolean getIntegrado() {
        return integrado;
    }

    public void setIntegrado(boolean integrado) {
        this.integrado = integrado;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Long getIdProcessoOriginouEmpenho() {
        if (empenho.getContrato() != null) {
            return empenho.getContrato().getId();
        }
        if (empenho.getExecucaoProcesso() != null) {
            return empenho.getExecucaoProcesso().getId();
        }
        if (empenho.getReconhecimentoDivida() != null) {
            return empenho.getReconhecimentoDivida().getId();
        }
        return null;
    }
}
