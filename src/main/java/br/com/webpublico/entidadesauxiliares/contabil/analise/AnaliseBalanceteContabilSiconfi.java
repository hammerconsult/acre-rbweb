package br.com.webpublico.entidadesauxiliares.contabil.analise;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.LancamentoContabil;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.PesquisaLCP;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class AnaliseBalanceteContabilSiconfi extends AssistenteBarraProgresso {

    private Conta conta;
    private PesquisaLCP.DebitoCredito debitoCredito;
    private UnidadeOrganizacional unidadeOrganizacional;
    private List<LancamentoContabil> lancamentos;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoAtual;
    private BigDecimal totalMovimentoCredito;
    private BigDecimal totalMovimentoDebito;
    private BigDecimal saldoContabilAtual;

    public AnaliseBalanceteContabilSiconfi() {

    }

    public AnaliseBalanceteContabilSiconfi(LancamentoContabil lancamento, Conta contaCredito, PesquisaLCP.DebitoCredito debitoCredito) {
        this.conta = contaCredito;
        this.unidadeOrganizacional = lancamento.getUnidadeOrganizacional();
        this.debitoCredito = debitoCredito;
        this.lancamentos = Lists.newArrayList();
        this.lancamentos.add(lancamento);
        this.saldoAnterior = null;
        this.saldoAtual = null;
        if (PesquisaLCP.DebitoCredito.CREDITO.equals(debitoCredito)) {
            this.totalMovimentoCredito = lancamento.getValor();
            this.totalMovimentoDebito = BigDecimal.ZERO;
        }
        if (PesquisaLCP.DebitoCredito.DEBITO.equals(debitoCredito)) {
            this.totalMovimentoCredito = BigDecimal.ZERO;
            this.totalMovimentoDebito = lancamento.getValor();
        }
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public List<LancamentoContabil> getLancamentos() {
        return lancamentos;
    }

    public void setLancamentos(List<LancamentoContabil> lancamentos) {
        this.lancamentos = lancamentos;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
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

    public PesquisaLCP.DebitoCredito getDebitoCredito() {
        return debitoCredito;
    }

    public void setDebitoCredito(PesquisaLCP.DebitoCredito debitoCredito) {
        this.debitoCredito = debitoCredito;
    }

    public BigDecimal getSaldoContabilAtual() {
        return saldoContabilAtual;
    }

    public void setSaldoContabilAtual(BigDecimal saldoContabilAtual) {
        this.saldoContabilAtual = saldoContabilAtual;
    }

    public BigDecimal getTotalMovimentoAtual() {
        if (getTotalMovimentoCredito() != null && getTotalMovimentoDebito() != null) {
            return getTotalMovimentoCredito().subtract(getTotalMovimentoDebito());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getDiferenca() {
        return getSaldoContabilAtual().subtract(getSaldoAtual());
    }
}
