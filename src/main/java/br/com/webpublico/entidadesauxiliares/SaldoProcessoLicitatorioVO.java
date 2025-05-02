package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoExecucaoProcesso;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class SaldoProcessoLicitatorioVO {

    private TipoExecucaoProcesso tipoProcesso;
    private SaldoProcessoLicitatorioOrigemVO saldoOrigemSelecionado;
    private List<SaldoProcessoLicitatorioOrigemVO> saldosAgrupadosPorOrigem;

    public SaldoProcessoLicitatorioVO(TipoExecucaoProcesso tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
        this.saldosAgrupadosPorOrigem = Lists.newArrayList();
    }

    public TipoExecucaoProcesso getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoExecucaoProcesso tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public SaldoProcessoLicitatorioOrigemVO getSaldoOrigemSelecionado() {
        return saldoOrigemSelecionado;
    }

    public void setSaldoOrigemSelecionado(SaldoProcessoLicitatorioOrigemVO saldoOrigemSelecionado) {
        this.saldoOrigemSelecionado = saldoOrigemSelecionado;
    }

    public List<SaldoProcessoLicitatorioOrigemVO> getSaldosAgrupadosPorOrigem() {
        return saldosAgrupadosPorOrigem;
    }

    public void setSaldosAgrupadosPorOrigem(List<SaldoProcessoLicitatorioOrigemVO> saldosAgrupadosPorOrigem) {
        this.saldosAgrupadosPorOrigem = saldosAgrupadosPorOrigem;
    }


    public BigDecimal getValorOriginalAtaOrDispensa() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioOrigemVO saldo : saldosAgrupadosPorOrigem) {
            valorTotal = valorTotal.add(saldo.getValorOriginalAtaOrDispensa());
        }
        return valorTotal;
    }

    public BigDecimal getValorOriginalAta() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioOrigemVO saldo : saldosAgrupadosPorOrigem) {
            valorTotal = valorTotal.add(saldo.getValorOriginalAta());
        }
        return valorTotal;
    }

    public BigDecimal getValorAtualAta() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioOrigemVO saldo : saldosAgrupadosPorOrigem) {
            valorTotal = valorTotal.add(saldo.getValorAtualAta());
        }
        return valorTotal;
    }

    public BigDecimal getValorAcrescimo() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioOrigemVO saldo : saldosAgrupadosPorOrigem) {
            valorTotal = valorTotal.add(saldo.getValorAcrescimo());
        }
        return valorTotal;
    }

    public BigDecimal getValorExecutado() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioOrigemVO saldo : saldosAgrupadosPorOrigem) {
            valorTotal = valorTotal.add(saldo.getValorExecutadoSemContrato());
        }
        return valorTotal;
    }

    public BigDecimal getValorEstornado() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioOrigemVO saldo : saldosAgrupadosPorOrigem) {
            valorTotal = valorTotal.add(saldo.getValorEstornadoSemContrato());
        }
        return valorTotal;
    }

    public BigDecimal getValorContratado() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioOrigemVO saldo : saldosAgrupadosPorOrigem) {
            valorTotal = valorTotal.add(saldo.getValorContratado());
        }
        return valorTotal;
    }

    public BigDecimal getSaldoDisponivel() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (hasSaldos()) {
            for (SaldoProcessoLicitatorioOrigemVO saldo : saldosAgrupadosPorOrigem) {
                valorTotal = valorTotal.add(saldo.getSaldoDisponivel());
            }
        }
        return valorTotal;
    }

    public BigDecimal getSaldoDisponivelComAcrescimo() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (hasSaldos()) {
            for (SaldoProcessoLicitatorioOrigemVO saldo : saldosAgrupadosPorOrigem) {
                valorTotal = valorTotal.add(saldo.getSaldoDisponivelComAcrescimo());
            }
        }
        return valorTotal;
    }

    public boolean hasSaldos() {
        return !Util.isListNullOrEmpty(saldosAgrupadosPorOrigem);
    }

    public boolean hasSaldoValor() {
        return getSaldoDisponivel().compareTo(BigDecimal.ZERO) > 0;
    }
}
