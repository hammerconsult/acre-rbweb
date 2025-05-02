package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.webreportdto.dto.contabil.SaldoFonteDespesaOcamentariaDTO;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class SaldoFonteDespesaOcamentariaVO {
    private BigDecimal dotacao;
    private BigDecimal reservado;
    private BigDecimal reservadoPorLicitacao;
    private BigDecimal alteracao;
    private BigDecimal suplementado;
    private BigDecimal reduzido;
    private BigDecimal empenhado;
    private BigDecimal liquidado;
    private BigDecimal pago;
    private Date dataSaldo;
    private List<MovimentoDespesaORCVO> movimentos;
    private List<MovimentoDespesaORCVO> movimentosFiltrados;

    public SaldoFonteDespesaOcamentariaVO() {
        dotacao = BigDecimal.ZERO;
        empenhado = BigDecimal.ZERO;
        liquidado = BigDecimal.ZERO;
        pago = BigDecimal.ZERO;
        reservado = BigDecimal.ZERO;
        suplementado = BigDecimal.ZERO;
        reduzido = BigDecimal.ZERO;
        reservadoPorLicitacao = BigDecimal.ZERO;
        movimentos = Lists.newArrayList();
        movimentosFiltrados = Lists.newArrayList();
    }

    public static List<SaldoFonteDespesaOcamentariaDTO> saldosToDto(List<SaldoFonteDespesaOcamentariaVO> saldos, boolean mostrarMovimentos, boolean hasMovimentosFiltrados) {
        List<SaldoFonteDespesaOcamentariaDTO> retorno = Lists.newArrayList();
        if (saldos != null && !saldos.isEmpty()) {
            int index = 0;
            for (SaldoFonteDespesaOcamentariaVO saldo : saldos) {
                retorno.add(saldoToDto(saldo, index == 0 ? null : saldos.get(index - 1).getSaldoAtual(), mostrarMovimentos, hasMovimentosFiltrados));
                index++;
            }
        }
        return retorno;
    }

    public static SaldoFonteDespesaOcamentariaDTO saldoToDto(SaldoFonteDespesaOcamentariaVO saldo, BigDecimal saldoAnterior, boolean mostrarMovimentos, boolean hasMovimentosFiltrados) {
        SaldoFonteDespesaOcamentariaDTO retorno = new SaldoFonteDespesaOcamentariaDTO();
        retorno.setDataSaldo(saldo.getDataSaldo());
        retorno.setDotacao(saldo.getDotacao());
        retorno.setAnulacao(saldo.getReduzido());
        retorno.setSuplementado(saldo.getSuplementado());
        retorno.setDotacaoAtualizada(saldo.getSaldoDotacaoAtual());
        retorno.setEmpenhado(saldo.getEmpenhado());
        retorno.setReservado(saldo.getReservado());
        retorno.setReservadoPorLicitacao(saldo.getReservadoPorLicitacao());
        retorno.setLiquidado(saldo.getLiquidado());
        retorno.setPago(saldo.getPago());
        retorno.setSaldoAtual(saldo.getSaldoAtual());
        retorno.setDiferencaComAnterior(saldoAnterior != null ? saldoAnterior.subtract(saldo.getSaldoAtual()) : BigDecimal.ZERO);
        if (mostrarMovimentos) {
            retorno.setMovimentos(hasMovimentosFiltrados ? MovimentoDespesaORCVO.movimentosToDto(saldo.getMovimentosFiltrados()) : MovimentoDespesaORCVO.movimentosToDto(saldo.getMovimentos()));
        }
        return retorno;
    }

    public Date getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(Date dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public BigDecimal getDotacao() {
        return dotacao;
    }

    public void setDotacao(BigDecimal dotacao) {
        this.dotacao = dotacao;
    }

    public BigDecimal getEmpenhado() {
        return empenhado;
    }

    public void setEmpenhado(BigDecimal empenhado) {
        this.empenhado = empenhado;
    }

    public BigDecimal getLiquidado() {
        return liquidado;
    }

    public void setLiquidado(BigDecimal liquidado) {
        this.liquidado = liquidado;
    }

    public BigDecimal getPago() {
        return pago;
    }

    public void setPago(BigDecimal pago) {
        this.pago = pago;
    }

    public BigDecimal getAlteracao() {
        return alteracao;
    }

    public void setAlteracao(BigDecimal alteracao) {
        this.alteracao = alteracao;
    }

    public BigDecimal getReservado() {
        return reservado;
    }

    public void setReservado(BigDecimal reservado) {
        this.reservado = reservado;
    }

    public BigDecimal getSuplementado() {
        return suplementado;
    }

    public void setSuplementado(BigDecimal suplementado) {
        this.suplementado = suplementado;
    }

    public BigDecimal getReduzido() {
        return reduzido;
    }

    public void setReduzido(BigDecimal reduzido) {
        this.reduzido = reduzido;
    }

    public BigDecimal getReservadoPorLicitacao() {
        return reservadoPorLicitacao;
    }

    public void setReservadoPorLicitacao(BigDecimal reservadoPorLicitacao) {
        this.reservadoPorLicitacao = reservadoPorLicitacao;
    }

    @Override
    public String toString() {
        return "Data do Saldo: " + dataSaldo + " - Dotação: " + dotacao;
    }

    public BigDecimal getSaldoAtual() {
        BigDecimal dotacaoAtual = getSaldoDotacaoAtual();
        BigDecimal debito = (this.getEmpenhado().add(this.getReservado().add(this.getReservadoPorLicitacao())));
        return dotacaoAtual.subtract(debito);
    }


    public BigDecimal getSaldoDotacaoAtual() {
        return this.getDotacao().add(getAlteracao());
    }

    public Boolean isNegativo() {
        return getSaldoAtual().compareTo(BigDecimal.ZERO) < 0;
    }

    public List<MovimentoDespesaORCVO> getMovimentos() {
        return movimentos;
    }

    public List<MovimentoDespesaORCVO> getMovimentosFiltrados() {
        return movimentosFiltrados;
    }

    public void setMovimentosFiltrados(List<MovimentoDespesaORCVO> movimentosFiltrados) {
        this.movimentosFiltrados = movimentosFiltrados;
    }

    public void setMovimentos(List<MovimentoDespesaORCVO> movimentos) {
        this.movimentos = movimentos;
    }
}
