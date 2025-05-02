package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoOperacaoORC;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Mateus on 08/02/2015.
 */
public class SaldoFonteDespesaOrcReprocessamento {
    private Date dataSaldo;
    private FonteDespesaORC fonteDespesaORC;
    private UnidadeOrganizacional unidadeOrganizacional;
    private BigDecimal dotacao;
    private BigDecimal empenhado;
    private BigDecimal liquidado;
    private BigDecimal pago;
    private BigDecimal alteracao;
    private BigDecimal reservado;
    private BigDecimal suplementado;
    private BigDecimal reduzido;
    private BigDecimal empenhadoProcessado;
    private BigDecimal liquidadoProcessado;
    private BigDecimal pagoProcessado;
    private BigDecimal empenhadoNaoProcessado;
    private BigDecimal liquidadoNaoProcessado;
    private BigDecimal pagoNaoProcessado;
    private BigDecimal reservadoPorLicitacao;
    private TipoOperacaoORC tipoOperacaoORC;
    private String idOrigem;
    private String classeOrigem;
    private String numeroMovimento;
    private String historico;

    public SaldoFonteDespesaOrcReprocessamento() {
        dotacao = BigDecimal.ZERO;
        empenhado = BigDecimal.ZERO;
        liquidado = BigDecimal.ZERO;
        pago = BigDecimal.ZERO;
        alteracao = BigDecimal.ZERO;
        reservado = BigDecimal.ZERO;
        suplementado = BigDecimal.ZERO;
        reduzido = BigDecimal.ZERO;
        reservadoPorLicitacao = BigDecimal.ZERO;
        empenhadoProcessado = BigDecimal.ZERO;
        liquidadoProcessado = BigDecimal.ZERO;
        pagoProcessado = BigDecimal.ZERO;
        empenhadoNaoProcessado = BigDecimal.ZERO;
        liquidadoNaoProcessado = BigDecimal.ZERO;
        pagoNaoProcessado = BigDecimal.ZERO;
    }

    public Date getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(Date dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
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

    public BigDecimal getEmpenhadoProcessado() {
        return empenhadoProcessado;
    }

    public void setEmpenhadoProcessado(BigDecimal empenhadoProcessado) {
        this.empenhadoProcessado = empenhadoProcessado;
    }

    public BigDecimal getLiquidadoProcessado() {
        return liquidadoProcessado;
    }

    public void setLiquidadoProcessado(BigDecimal liquidadoProcessado) {
        this.liquidadoProcessado = liquidadoProcessado;
    }

    public BigDecimal getPagoProcessado() {
        return pagoProcessado;
    }

    public void setPagoProcessado(BigDecimal pagoProcessado) {
        this.pagoProcessado = pagoProcessado;
    }

    public BigDecimal getEmpenhadoNaoProcessado() {
        return empenhadoNaoProcessado;
    }

    public void setEmpenhadoNaoProcessado(BigDecimal empenhadoNaoProcessado) {
        this.empenhadoNaoProcessado = empenhadoNaoProcessado;
    }

    public BigDecimal getLiquidadoNaoProcessado() {
        return liquidadoNaoProcessado;
    }

    public void setLiquidadoNaoProcessado(BigDecimal liquidadoNaoProcessado) {
        this.liquidadoNaoProcessado = liquidadoNaoProcessado;
    }

    public BigDecimal getPagoNaoProcessado() {
        return pagoNaoProcessado;
    }

    public void setPagoNaoProcessado(BigDecimal pagoNaoProcessado) {
        this.pagoNaoProcessado = pagoNaoProcessado;
    }

    public BigDecimal getReservadoPorLicitacao() {
        return reservadoPorLicitacao;
    }

    public void setReservadoPorLicitacao(BigDecimal reservadoPorLicitacao) {
        this.reservadoPorLicitacao = reservadoPorLicitacao;
    }

    public TipoOperacaoORC getTipoOperacaoORC() {
        return tipoOperacaoORC;
    }

    public void setTipoOperacaoORC(TipoOperacaoORC tipoOperacaoORC) {
        this.tipoOperacaoORC = tipoOperacaoORC;
    }

    public String getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(String idOrigem) {
        this.idOrigem = idOrigem;
    }

    public String getClasseOrigem() {
        return classeOrigem;
    }

    public void setClasseOrigem(String classeOrigem) {
        this.classeOrigem = classeOrigem;
    }

    public String getNumeroMovimento() {
        return numeroMovimento;
    }

    public void setNumeroMovimento(String numeroMovimento) {
        this.numeroMovimento = numeroMovimento;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }
}
