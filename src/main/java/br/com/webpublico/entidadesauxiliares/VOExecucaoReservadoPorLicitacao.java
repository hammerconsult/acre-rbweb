package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.FonteDespesaORC;

import java.math.BigDecimal;

public class VOExecucaoReservadoPorLicitacao {

    private FonteDespesaORC fonteDespesaORC;
    private BigDecimal solicitacoEmpenho;
    private BigDecimal empenho;
    private BigDecimal valorMovimento;
    private BigDecimal saldoFonteDespesa;
    private BigDecimal saldoAtual;
    private BigDecimal reservadoPorLicitacao;
    private BigDecimal valorFinal;

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public BigDecimal getSolicitacoEmpenho() {
        return solicitacoEmpenho;
    }

    public void setSolicitacoEmpenho(BigDecimal solicitacoEmpenho) {
        this.solicitacoEmpenho = solicitacoEmpenho;
    }

    public BigDecimal getEmpenho() {
        return empenho;
    }

    public void setEmpenho(BigDecimal empenho) {
        this.empenho = empenho;
    }

    public BigDecimal getValorMovimento() {
        return valorMovimento;
    }

    public void setValorMovimento(BigDecimal valorMovimento) {
        this.valorMovimento = valorMovimento;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public BigDecimal getReservadoPorLicitacao() {
        return reservadoPorLicitacao;
    }

    public void setReservadoPorLicitacao(BigDecimal reservadoPorLicitacao) {
        this.reservadoPorLicitacao = reservadoPorLicitacao;
    }

    public BigDecimal getSaldoFonteDespesa() {
        return saldoFonteDespesa;
    }

    public void setSaldoFonteDespesa(BigDecimal saldoFonteDespesa) {
        this.saldoFonteDespesa = saldoFonteDespesa;
    }

    public boolean hasValorFinalNegativo(){
        return valorFinal.compareTo(BigDecimal.ZERO) <0;
    }

    public boolean hasSaldoAtualNegativo(){
        return saldoAtual.compareTo(BigDecimal.ZERO) <0;
    }
}
