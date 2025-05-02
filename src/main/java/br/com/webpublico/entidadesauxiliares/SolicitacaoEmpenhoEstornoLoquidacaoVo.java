package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Liquidacao;
import br.com.webpublico.entidades.Pagamento;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class SolicitacaoEmpenhoEstornoLoquidacaoVo implements Serializable, Comparable<SolicitacaoEmpenhoEstornoLoquidacaoVo> {

    private Liquidacao liquidacao;
    private BigDecimal saldo;
    private Boolean selecionado;
    private SolicitacaoEmpenhoEstornoVo voExecucaoContratoEstorno;
    private BigDecimal valorTotalPagamento;
    private List<Pagamento> pagamentos;

    public SolicitacaoEmpenhoEstornoLoquidacaoVo() {
        this.pagamentos = Lists.newArrayList();
        this.valorTotalPagamento = BigDecimal.ZERO;
        this.selecionado = false;
    }

    public BigDecimal getValorTotalPagamento() {
        return valorTotalPagamento;
    }

    public void setValorTotalPagamento(BigDecimal valorTotalPagamento) {
        this.valorTotalPagamento = valorTotalPagamento;
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public SolicitacaoEmpenhoEstornoVo getVoExecucaoContratoEstorno() {
        return voExecucaoContratoEstorno;
    }

    public void setVoExecucaoContratoEstorno(SolicitacaoEmpenhoEstornoVo voExecucaoContratoEstorno) {
        this.voExecucaoContratoEstorno = voExecucaoContratoEstorno;
    }

    public boolean hasSaldoDisponivel() {
        return saldo != null && saldo.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolicitacaoEmpenhoEstornoLoquidacaoVo that = (SolicitacaoEmpenhoEstornoLoquidacaoVo) o;
        return Objects.equals(liquidacao, that.liquidacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(liquidacao);
    }

    @Override
    public int compareTo(SolicitacaoEmpenhoEstornoLoquidacaoVo o) {
        if (o.getLiquidacao() != null && getLiquidacao() != null) {
            return getLiquidacao().getNumero().compareTo(o.getLiquidacao().getNumero());
        }
        return 0;
    }
}


