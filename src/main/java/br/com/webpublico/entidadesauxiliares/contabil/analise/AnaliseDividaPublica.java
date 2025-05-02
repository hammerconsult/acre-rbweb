package br.com.webpublico.entidadesauxiliares.contabil.analise;

import br.com.webpublico.entidades.ContaDeDestinacao;
import br.com.webpublico.entidades.DividaPublica;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.SaldoDividaPublicaReprocessamento;
import br.com.webpublico.enums.OperacaoMovimentoDividaPublica;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class AnaliseDividaPublica extends AssistenteBarraProgresso {

    private UnidadeOrganizacional unidadeOrganizacional;
    private DividaPublica dividapublica;
    private ContaDeDestinacao contaDeDestinacao;
    private List<SaldoDividaPublicaReprocessamento> movimentos;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoUltimo;
    private BigDecimal totalMovimentoCredito;
    private BigDecimal totalMovimentoDebito;

    public AnaliseDividaPublica() {

    }

    public AnaliseDividaPublica(SaldoDividaPublicaReprocessamento reprocessamento) {
        this.unidadeOrganizacional = reprocessamento.getUnidadeOrganizacional();
        this.dividapublica = reprocessamento.getDividaPublica();
        this.contaDeDestinacao = reprocessamento.getContaDeDestinacao();
        this.movimentos = Lists.newArrayList();
        this.movimentos.add(reprocessamento);
        if (OperacaoMovimentoDividaPublica.getTiposOperacaoSomar().contains(reprocessamento.getOperacaoMovimentoDividaPublica())) {
            this.totalMovimentoCredito = reprocessamento.getValor();
            this.totalMovimentoDebito = BigDecimal.ZERO;
        } else {
            this.totalMovimentoDebito = reprocessamento.getValor();
            this.totalMovimentoCredito = BigDecimal.ZERO;
        }
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public DividaPublica getDividapublica() {
        return dividapublica;
    }

    public void setDividapublica(DividaPublica dividapublica) {
        this.dividapublica = dividapublica;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getSaldoUltimo() {
        return saldoUltimo;
    }

    public void setSaldoUltimo(BigDecimal saldoUltimo) {
        this.saldoUltimo = saldoUltimo;
    }

    public void setMovimentos(List<SaldoDividaPublicaReprocessamento> movimentos) {
        this.movimentos = movimentos;
    }

    public List<SaldoDividaPublicaReprocessamento> getMovimentos() {
        return movimentos;
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

    public BigDecimal getSaldoAtual() {
        if (saldoUltimo != null) {
            return saldoUltimo;
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
        BigDecimal saldoAnteriorSaldoDoDia = saldoAnterior != null ? saldoAnterior : BigDecimal.ZERO;
        BigDecimal totalMovimento = saldoAnteriorSaldoDoDia.add(getTotalMovimentoAtual());
        return totalMovimento.subtract(getSaldoAtual());
    }

}
