package br.com.webpublico.entidadesauxiliares.contabil.analise;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaDeDestinacao;
import br.com.webpublico.entidades.SaldoExtraorcamentario;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.util.AssistenteBarraProgresso;

import java.math.BigDecimal;

public class AnaliseContaExtra extends AssistenteBarraProgresso {

    private Conta contaExtraorcamentaria;
    private UnidadeOrganizacional unidadeOrganizacional;
    private ContaDeDestinacao contaDeDestinacao;
    private SaldoExtraorcamentario saldoAnterior;
    private SaldoExtraorcamentario saldoUltimo;
    private BigDecimal totalMovimentoCredito;
    private BigDecimal totalMovimentoDebito;

    public AnaliseContaExtra() {

    }

    public AnaliseContaExtra(UnidadeOrganizacional unidadeOrganizacional, Conta contaExtraorcamentaria, ContaDeDestinacao contaDeDestinacao, BigDecimal credito, BigDecimal debito) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.contaExtraorcamentaria = contaExtraorcamentaria;
        this.contaDeDestinacao = contaDeDestinacao;
        this.totalMovimentoCredito = credito;
        this.totalMovimentoDebito = debito;
    }

    public Conta getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(Conta contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public SaldoExtraorcamentario getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(SaldoExtraorcamentario saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public SaldoExtraorcamentario getSaldoUltimo() {
        return saldoUltimo;
    }

    public void setSaldoUltimo(SaldoExtraorcamentario saldoUltimo) {
        this.saldoUltimo = saldoUltimo;
    }

    public BigDecimal getSaldoAtual() {
        if (saldoUltimo != null && saldoUltimo.getId() != null) {
            return saldoUltimo.getValor();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalMovimentoAtual() {
        if (getTotalMovimentoCredito() != null && getTotalMovimentoDebito() != null) {
            return getTotalMovimentoCredito().subtract(getTotalMovimentoDebito());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getDiferenca() {
        BigDecimal saldoAnteriorSaldoDoDia = saldoAnterior != null ? saldoAnterior.getValor() : BigDecimal.ZERO;
        BigDecimal totalMovimento = saldoAnteriorSaldoDoDia.add(getTotalMovimentoAtual());
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
}
