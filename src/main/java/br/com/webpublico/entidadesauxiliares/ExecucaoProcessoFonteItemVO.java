package br.com.webpublico.entidadesauxiliares;


import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ExecucaoProcessoItem;
import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExecucaoProcessoFonteItemVO implements Serializable, Comparable<ExecucaoProcessoFonteItemVO> {

    private ExecucaoProcessoFonteVO execucaoProcessoFonteVO;
    private ExecucaoProcessoItem execucaoProcessoItem;
    private Conta contaDespesa;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal quantidadeReservada;
    private BigDecimal valorReservado;
    private Long criadoEm;

    public ExecucaoProcessoFonteItemVO() {
        criadoEm = System.nanoTime();
        quantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
        quantidadeReservada = BigDecimal.ZERO;
        valorReservado = BigDecimal.ZERO;
    }

    public ExecucaoProcessoItem getExecucaoProcessoItem() {
        return execucaoProcessoItem;
    }

    public void setExecucaoProcessoItem(ExecucaoProcessoItem execucaoProcessoItem) {
        this.execucaoProcessoItem = execucaoProcessoItem;
    }

    public br.com.webpublico.entidades.Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
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

    public BigDecimal getQuantidadeReservada() {
        return quantidadeReservada;
    }

    public void setQuantidadeReservada(BigDecimal quantidadeReservada) {
        this.quantidadeReservada = quantidadeReservada;
    }

    public BigDecimal getValorReservado() {
        return valorReservado;
    }

    public void setValorReservado(BigDecimal valorReservado) {
        this.valorReservado = valorReservado;
    }

    public ExecucaoProcessoFonteVO getExecucaoProcessoFonteVO() {
        return execucaoProcessoFonteVO;
    }

    public void setExecucaoProcessoFonteVO(ExecucaoProcessoFonteVO execucaoProcessoFonteVO) {
        this.execucaoProcessoFonteVO = execucaoProcessoFonteVO;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getQuantidadeDisponivel() {
        try {
            return getExecucaoProcessoItem().getQuantidade().subtract(quantidadeReservada);
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorDisponivel() {
        try {
            return getExecucaoProcessoItem().getValorTotal().subtract(valorReservado);
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public void calcularValorTotal() {
        if (hasQuantidade()) {
            BigDecimal total = getQuantidade().multiply(getValorUnitario());
            setValorTotal(total.setScale(2, RoundingMode.HALF_EVEN));
        } else {
            setValorTotal(BigDecimal.ZERO);
        }
    }

    public Boolean hasQuantidade() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    public Boolean hasValor() {
        return valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public int compareTo(ExecucaoProcessoFonteItemVO o) {
        try {
            return ComparisonChain.start()
                .compare(getExecucaoProcessoItem().getItemProcessoCompra().getNumero(), o.getExecucaoProcessoItem().getItemProcessoCompra().getNumero())
                .result();
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
