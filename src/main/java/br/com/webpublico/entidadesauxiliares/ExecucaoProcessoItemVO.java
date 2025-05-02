package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemProcessoDeCompra;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import com.google.common.collect.ComparisonChain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExecucaoProcessoItemVO implements Comparable<ExecucaoProcessoItemVO> {

    private ItemProcessoDeCompra itemProcessoCompra;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private SaldoProcessoLicitatorioItemVO itemSaldoVO;

    public ExecucaoProcessoItemVO() {
        valorTotal = BigDecimal.ZERO;
        quantidade = BigDecimal.ZERO;
    }

    public ItemProcessoDeCompra getItemProcessoCompra() {
        return itemProcessoCompra;
    }

    public void setItemProcessoCompra(ItemProcessoDeCompra itemProcessoCompra) {
        this.itemProcessoCompra = itemProcessoCompra;
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
        if (valorTotal != null) {
            return valorTotal.setScale(2, RoundingMode.FLOOR);
        }
        return BigDecimal.ZERO;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public SaldoProcessoLicitatorioItemVO getItemSaldoVO() {
        return itemSaldoVO;
    }

    public void setItemSaldoVO(SaldoProcessoLicitatorioItemVO itemSaldoVO) {
        this.itemSaldoVO = itemSaldoVO;
    }

    public ObjetoCompra getObjetoCompra() {
        try {
            return itemProcessoCompra.getObjetoCompra();
        } catch (Exception e) {
            return null;
        }
    }

    public String getNumeroDescricao() {
        try {
            return itemProcessoCompra.getNumero() + " - " + itemProcessoCompra.getDescricao();
        } catch (Exception e) {
            return null;
        }
    }

    public TipoMascaraUnidadeMedida getMascaraQuantidade() {
        try {
            return itemProcessoCompra.getItemSolicitacaoMaterial().getUnidadeMedida().getMascaraQuantidade();
        } catch (Exception ex) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitario() {
        try {
            return itemProcessoCompra.getItemSolicitacaoMaterial().getUnidadeMedida().getMascaraValorUnitario();
        } catch (Exception ex) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
    }

    @Override
    public String toString() {
        try {
            return itemProcessoCompra.getNumero() + " - " + itemProcessoCompra.getDescricao();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public void calcularValorTotal() {
        setValorTotal(getQuantidade().multiply(getValorUnitario()).setScale(2, RoundingMode.HALF_EVEN));
    }

    public boolean hasQuantidade() {
        return getQuantidade() != null && getQuantidade().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValor() {
        return valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public int compareTo(ExecucaoProcessoItemVO o) {
        try {
            if (o.getItemProcessoCompra() != null && getItemProcessoCompra() != null) {
                return ComparisonChain.start().compare(getItemProcessoCompra().getLoteProcessoDeCompra().getNumero(), o.getItemProcessoCompra().getLoteProcessoDeCompra().getNumero())
                    .compare(getItemProcessoCompra().getNumero(), o.getItemProcessoCompra().getNumero()).result();
            }
            return 0;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public boolean isExecucaoPorValor() {
        return getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().isTipoControlePorValor();
    }

    public boolean isExecucaoPorQuantidade() {
        return getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().isTipoControlePorQuantidade();
    }
}
