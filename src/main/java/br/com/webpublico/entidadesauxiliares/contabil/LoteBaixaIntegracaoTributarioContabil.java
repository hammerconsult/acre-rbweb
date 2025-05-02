package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.LoteBaixa;

import java.math.BigDecimal;
import java.util.Date;

public class LoteBaixaIntegracaoTributarioContabil {

    private LoteBaixa loteBaixa;
    private String codigoLote;
    private Date dataLote;
    private BigDecimal valorLote;
    private BigDecimal valorReceita;
    private BigDecimal valorCreditoReceber;
    private BigDecimal valorDividaAtiva;


    public LoteBaixaIntegracaoTributarioContabil() {
        this.valorReceita = BigDecimal.ZERO;
        this.valorCreditoReceber = BigDecimal.ZERO;
        this.valorDividaAtiva = BigDecimal.ZERO;
        this.valorLote = BigDecimal.ZERO;
    }

    public LoteBaixaIntegracaoTributarioContabil(LoteBaixa loteBaixa, Date dataLote, BigDecimal valorLote, BigDecimal valorReceita) {
        this.loteBaixa = loteBaixa;
        this.codigoLote = loteBaixa != null ? loteBaixa.getCodigoLote() : "";
        this.dataLote = dataLote;
        this.valorLote = valorLote;
        this.valorReceita = valorReceita;
        this.valorCreditoReceber = BigDecimal.ZERO;
        this.valorDividaAtiva = BigDecimal.ZERO;
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public Date getDataLote() {
        return dataLote;
    }

    public void setDataLote(Date dataLote) {
        this.dataLote = dataLote;
    }

    public BigDecimal getValorLote() {
        return valorLote;
    }

    public void setValorLote(BigDecimal valorLote) {
        this.valorLote = valorLote;
    }

    public BigDecimal getValorReceita() {
        return valorReceita;
    }

    public void setValorReceita(BigDecimal valorReceita) {
        this.valorReceita = valorReceita;
    }

    public BigDecimal getValorCreditoReceber() {
        return valorCreditoReceber;
    }

    public void setValorCreditoReceber(BigDecimal valorCreditoReceber) {
        this.valorCreditoReceber = valorCreditoReceber;
    }

    public BigDecimal getValorDividaAtiva() {
        return valorDividaAtiva;
    }

    public void setValorDividaAtiva(BigDecimal valorDividaAtiva) {
        this.valorDividaAtiva = valorDividaAtiva;
    }
}

