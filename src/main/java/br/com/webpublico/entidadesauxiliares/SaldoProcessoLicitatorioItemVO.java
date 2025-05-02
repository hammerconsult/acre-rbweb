package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemProcessoDeCompra;
import br.com.webpublico.entidades.ItemPropostaFornecedor;
import br.com.webpublico.enums.OrigemSaldoExecucaoProcesso;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SaldoProcessoLicitatorioItemVO {

    private ItemPropostaFornecedor itemPropostaFornecedor;
    private ItemProcessoDeCompra itemProcessoCompra;
    private OrigemSaldoExecucaoProcesso origemSaldo;
    private SaldoProcessoLicitatorioOrigemVO.NaturezaSaldo naturezaSaldo;
    private Long idProcesso;
    private Long idFornecedor;
    private BigDecimal quantidadeProcesso;
    private BigDecimal quantidadeAta;
    private BigDecimal quantidadeExecutada;
    private BigDecimal quantidadeEstornada;
    private BigDecimal quantidadeContratada;
    private BigDecimal quantidadeAcrescimo;
    private BigDecimal valorUnitarioHomologado;
    private BigDecimal valorlHomologado;
    private BigDecimal valorAta;
    private BigDecimal valorContratado;
    private BigDecimal valorExecutado;
    private BigDecimal valorEstornado;
    private BigDecimal valorAcrescimo;

    public SaldoProcessoLicitatorioItemVO() {
        quantidadeProcesso = BigDecimal.ZERO;
        quantidadeAta = BigDecimal.ZERO;
        quantidadeExecutada = BigDecimal.ZERO;
        quantidadeEstornada = BigDecimal.ZERO;
        quantidadeContratada = BigDecimal.ZERO;
        quantidadeAcrescimo = BigDecimal.ZERO;
        valorAta = BigDecimal.ZERO;
        valorExecutado = BigDecimal.ZERO;
        valorEstornado = BigDecimal.ZERO;
        valorContratado = BigDecimal.ZERO;
        valorlHomologado = BigDecimal.ZERO;
    }

    public OrigemSaldoExecucaoProcesso getOrigemSaldo() {
        return origemSaldo;
    }

    public void setOrigemSaldo(OrigemSaldoExecucaoProcesso origemSaldo) {
        this.origemSaldo = origemSaldo;
    }

    public SaldoProcessoLicitatorioOrigemVO.NaturezaSaldo getNaturezaSaldo() {
        return naturezaSaldo;
    }

    public void setNaturezaSaldo(SaldoProcessoLicitatorioOrigemVO.NaturezaSaldo naturezaSaldo) {
        this.naturezaSaldo = naturezaSaldo;
    }

    public BigDecimal getQuantidadeAcrescimo() {
        return quantidadeAcrescimo;
    }

    public void setQuantidadeAcrescimo(BigDecimal quantidadeAcrescimo) {
        this.quantidadeAcrescimo = quantidadeAcrescimo;
    }

    public BigDecimal getValorlHomologado() {
        return valorlHomologado;
    }

    public void setValorlHomologado(BigDecimal valorlHomologado) {
        this.valorlHomologado = valorlHomologado;
    }

    public BigDecimal getQuantidadeAta() {
        return quantidadeAta;
    }

    public void setQuantidadeAta(BigDecimal quantidadeAta) {
        this.quantidadeAta = quantidadeAta;
    }

    public ItemPropostaFornecedor getItemPropostaFornecedor() {
        return itemPropostaFornecedor;
    }

    public void setItemPropostaFornecedor(ItemPropostaFornecedor itemPropostaFornecedor) {
        this.itemPropostaFornecedor = itemPropostaFornecedor;
    }

    public BigDecimal getQuantidadeProcesso() {
        return quantidadeProcesso;
    }

    public void setQuantidadeProcesso(BigDecimal quantidadeProcesso) {
        this.quantidadeProcesso = quantidadeProcesso;
    }

    public BigDecimal getQuantidadeContratada() {
        return quantidadeContratada;
    }

    public void setQuantidadeContratada(BigDecimal quantidadeContratada) {
        this.quantidadeContratada = quantidadeContratada;
    }

    public BigDecimal getValorUnitarioHomologado() {
        return valorUnitarioHomologado;
    }

    public void setValorUnitarioHomologado(BigDecimal valorUnitarioHomologado) {
        this.valorUnitarioHomologado = valorUnitarioHomologado;
    }

    public BigDecimal getValorAta() {
        return valorAta;
    }

    public void setValorAta(BigDecimal valorAta) {
        this.valorAta = valorAta;
    }

    public BigDecimal getValorContratado() {
        return valorContratado;
    }

    public void setValorContratado(BigDecimal valorContratado) {
        this.valorContratado = valorContratado;
    }

    public BigDecimal getQuantidadeExecutada() {
        return quantidadeExecutada;
    }

    public void setQuantidadeExecutada(BigDecimal quantidadeExecutada) {
        this.quantidadeExecutada = quantidadeExecutada;
    }

    public BigDecimal getQuantidadeEstornada() {
        return quantidadeEstornada;
    }

    public void setQuantidadeEstornada(BigDecimal quantidadeEstornada) {
        this.quantidadeEstornada = quantidadeEstornada;
    }

    public BigDecimal getValorExecutado() {
        return valorExecutado;
    }

    public void setValorExecutado(BigDecimal valorExecutado) {
        this.valorExecutado = valorExecutado;
    }

    public BigDecimal getValorEstornado() {
        return valorEstornado;
    }

    public void setValorEstornado(BigDecimal valorEstornado) {
        this.valorEstornado = valorEstornado;
    }

    public ItemProcessoDeCompra getItemProcessoCompra() {
        return itemProcessoCompra;
    }

    public void setItemProcessoCompra(ItemProcessoDeCompra itemProcessoCompra) {
        this.itemProcessoCompra = itemProcessoCompra;
    }

    public Long getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Long idProcesso) {
        this.idProcesso = idProcesso;
    }

    public Long getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Long idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public BigDecimal getValorAcrescimo() {
        return valorAcrescimo;
    }

    public void setValorAcrescimo(BigDecimal valorAcrescimo) {
        this.valorAcrescimo = valorAcrescimo;
    }

    public BigDecimal getQuantidadeDisponivel() {
        if (getOrigemSaldo().isAta()) {
            return quantidadeAta.subtract(quantidadeExecutada).subtract(quantidadeContratada).add(quantidadeEstornada);
        } else if (getOrigemSaldo().isDispensa()) {
            return quantidadeProcesso.subtract(quantidadeExecutada).subtract(quantidadeContratada).add(quantidadeEstornada);
        }
        return quantidadeAcrescimo.subtract(quantidadeExecutada).add(quantidadeEstornada);
    }

    public BigDecimal getSaldoDisponivel() {
        if (getOrigemSaldo().isAta()) {
            return valorAta.subtract(valorExecutado).subtract(valorContratado).add(valorEstornado);
        } else if (getOrigemSaldo().isDispensa()) {
            return valorlHomologado.subtract(valorExecutado).subtract(valorContratado).add(valorEstornado);
        }
        return valorAcrescimo.subtract(valorExecutado).add(valorEstornado);
    }

    public BigDecimal getSaldoDisponivelComAcrescimo() {
        if (getOrigemSaldo().isAta()) {
            return valorAta.subtract(valorExecutado).subtract(valorContratado).add(valorEstornado).add(valorAcrescimo);
        } else if (getOrigemSaldo().isDispensa()) {
            return valorlHomologado.subtract(valorExecutado).subtract(valorContratado).add(valorEstornado);
        }
        return valorAcrescimo.subtract(valorExecutado).add(valorEstornado);
    }

    public BigDecimal calcularValorTotalAta() {
        try {
            if (getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().getTipoControle().isTipoControlePorQuantidade()) {
                BigDecimal total = getQuantidadeAta().multiply(getValorUnitarioHomologado());
                return total.setScale(2, RoundingMode.HALF_EVEN);
            }
            return getValorUnitarioHomologado();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
