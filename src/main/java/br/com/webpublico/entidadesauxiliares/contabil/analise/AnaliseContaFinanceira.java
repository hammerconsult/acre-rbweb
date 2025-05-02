package br.com.webpublico.entidadesauxiliares.contabil.analise;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.PesquisaLCP;
import br.com.webpublico.entidadesauxiliares.ReprocessamentoSubConta;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class AnaliseContaFinanceira extends AssistenteBarraProgresso {

    private SubConta subConta;
    private UnidadeOrganizacional unidadeOrganizacional;
    private ContaDeDestinacao contaDeDestinacao;
    private List<ReprocessamentoSubConta> reprocessamentoSubContas;
    private SaldoSubConta saldoAnterior;
    private SaldoSubConta saldoSubConta;
    private BigDecimal totalMovimentoCredito;
    private BigDecimal totalMovimentoDebito;

    public AnaliseContaFinanceira() {

    }

    public AnaliseContaFinanceira(ReprocessamentoSubConta reprocessamentoSubConta) {
        this.subConta = reprocessamentoSubConta.getSubConta();
        this.contaDeDestinacao = reprocessamentoSubConta.getContaDeDestinacao();
        this.unidadeOrganizacional = reprocessamentoSubConta.getUnidadeOrganizacional();
        this.reprocessamentoSubContas = Lists.newArrayList();
        this.reprocessamentoSubContas.add(reprocessamentoSubConta);
        this.totalMovimentoCredito = reprocessamentoSubConta.getValorCredito();
        this.totalMovimentoDebito = reprocessamentoSubConta.getValorDebito();
    }

    public AnaliseContaFinanceira(SubConta subConta, ContaDeDestinacao contaDeDestinacao, UnidadeOrganizacional unidadeOrganizacional) {
        this.subConta = subConta;
        this.contaDeDestinacao = contaDeDestinacao;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.reprocessamentoSubContas = Lists.newArrayList();
        this.totalMovimentoCredito = BigDecimal.ZERO;
        this.totalMovimentoDebito = BigDecimal.ZERO;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public List<ReprocessamentoSubConta> getReprocessamentoSubContas() {
        return reprocessamentoSubContas;
    }

    public void setReprocessamentoSubContas(List<ReprocessamentoSubConta> reprocessamentoSubContas) {
        this.reprocessamentoSubContas = reprocessamentoSubContas;
    }

    public SaldoSubConta getSaldoSubConta() {
        return saldoSubConta;
    }

    public void setSaldoSubConta(SaldoSubConta saldoSubConta) {
        this.saldoSubConta = saldoSubConta;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public BigDecimal getSaldoAtual() {
        if (saldoSubConta != null && saldoSubConta.getId() != null) {
            return saldoSubConta.getTotalCredito().subtract(saldoSubConta.getTotalDebito());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalMovimentoAtual() {
        if (getTotalMovimentoCredito() != null && getTotalMovimentoDebito() != null) {
            BigDecimal movimentoAtual = getTotalMovimentoCredito().subtract(getTotalMovimentoDebito());
            BigDecimal saldoAnteriorSaldoDoDia = saldoAnterior != null ? saldoAnterior.getSaldoDoDia() : BigDecimal.ZERO;
            BigDecimal totalMovimento = saldoAnteriorSaldoDoDia.add(movimentoAtual);
            return totalMovimento;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getDiferenca() {
        BigDecimal totalMovimento = getTotalMovimentoAtual();
        return totalMovimento.subtract(getSaldoAtual());
    }

    public BigDecimal getTotalMovimentoCredito() {
        return totalMovimentoCredito;
    }

    public void setTotalMovimentoCredito(BigDecimal totalMovimentoCredito) {
        this.totalMovimentoCredito = totalMovimentoCredito;
    }

    public BigDecimal getTotalMovimentoDebito() {
        return totalMovimentoDebito;
    }

    public void setTotalMovimentoDebito(BigDecimal totalMovimentoDebito) {
        this.totalMovimentoDebito = totalMovimentoDebito;
    }

    public SaldoSubConta getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(SaldoSubConta saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }
}
