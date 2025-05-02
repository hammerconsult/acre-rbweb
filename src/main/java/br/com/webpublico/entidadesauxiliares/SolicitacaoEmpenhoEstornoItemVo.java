package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ExecucaoContratoItem;
import br.com.webpublico.entidades.ExecucaoProcessoItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class SolicitacaoEmpenhoEstornoItemVo {

    private ExecucaoContratoItem execucaoContratoItem;
    private ExecucaoProcessoItem execucaoProcessoItem;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal valorEmExecucao;
    private BigDecimal valorEmRequisicao;
    private BigDecimal valorEstornoRequisicao;
    private BigDecimal valorEstornadoExecucao;

    public SolicitacaoEmpenhoEstornoItemVo() {
        quantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
        valorEmExecucao = BigDecimal.ZERO;
        valorEmRequisicao = BigDecimal.ZERO;
        valorEstornoRequisicao = BigDecimal.ZERO;
        valorEstornadoExecucao = BigDecimal.ZERO;
    }

    public BigDecimal getValorEmExecucao() {
        return valorEmExecucao;
    }

    public void setValorEmExecucao(BigDecimal valorEmExecucao) {
        this.valorEmExecucao = valorEmExecucao;
    }

    public BigDecimal getValorEmRequisicao() {
        return valorEmRequisicao;
    }

    public void setValorEmRequisicao(BigDecimal valorEmRequisicao) {
        this.valorEmRequisicao = valorEmRequisicao;
    }

    public BigDecimal getValorEstornoRequisicao() {
        return valorEstornoRequisicao;
    }

    public void setValorEstornoRequisicao(BigDecimal valorEstornoRequisicao) {
        this.valorEstornoRequisicao = valorEstornoRequisicao;
    }

    public Boolean getSelecionado() {
        return valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getValorUtilizado() {
        return getValorEmRequisicao().add(getValorEstornadoExecucao()).subtract(getValorEstornoRequisicao());
    }

    public BigDecimal getValorEstornadoExecucao() {
        return valorEstornadoExecucao;
    }

    public void setValorEstornadoExecucao(BigDecimal valorEstornadoExecucao) {
        this.valorEstornadoExecucao = valorEstornadoExecucao;
    }

    public ExecucaoContratoItem getExecucaoContratoItem() {
        return execucaoContratoItem;
    }

    public void setExecucaoContratoItem(ExecucaoContratoItem execucaoContratoItem) {
        this.execucaoContratoItem = execucaoContratoItem;
    }

    public ExecucaoProcessoItem getExecucaoProcessoItem() {
        return execucaoProcessoItem;
    }

    public void setExecucaoProcessoItem(ExecucaoProcessoItem execucaoProcessoItem) {
        this.execucaoProcessoItem = execucaoProcessoItem;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void calcularValorTotal() {
        setValorTotal(BigDecimal.ZERO);
        if (quantidade.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal total = getQuantidade().multiply(getValorUnitario());
            setValorTotal(total.setScale(2, RoundingMode.HALF_EVEN));
        }
    }

    public void calcularQuantidade() {
        setQuantidade(BigDecimal.ZERO);
        if (getValorTotal().compareTo(BigDecimal.ZERO) > 0 && getValorUnitario().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal quantidade = getValorTotal().divide(getValorUnitario(), 4, RoundingMode.HALF_EVEN);
            setQuantidade(quantidade);
        }
    }

    public BigDecimal getSaldoDisponivel() {
        if (execucaoContratoItem != null ||execucaoProcessoItem !=null) {
            return valorEmExecucao.subtract(valorEmRequisicao).subtract(valorEstornadoExecucao).add(valorEstornoRequisicao);
        }
        return BigDecimal.ZERO;
    }

    public boolean isTipoControleQuantidade() {
        if (execucaoContratoItem != null) {
            return execucaoContratoItem.getItemContrato().getControleQuantidade();
        }
        return execucaoProcessoItem.isExecucaoPorQuantidade();
    }

    public boolean isTipoControleValor() {
        if (execucaoContratoItem != null) {
            return execucaoContratoItem.getItemContrato().getControleValor();
        }
        return execucaoProcessoItem.isExecucaoPorValor();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolicitacaoEmpenhoEstornoItemVo that = (SolicitacaoEmpenhoEstornoItemVo) o;
        if (execucaoContratoItem !=null) {
            return Objects.equals(execucaoContratoItem, that.execucaoContratoItem);
        }
        return Objects.equals(execucaoProcessoItem, that.execucaoProcessoItem);
    }

    @Override
    public int hashCode() {
        if (execucaoContratoItem !=null) {
            return Objects.hash(execucaoContratoItem);
        }
        return Objects.hash(execucaoProcessoItem);
    }
}
