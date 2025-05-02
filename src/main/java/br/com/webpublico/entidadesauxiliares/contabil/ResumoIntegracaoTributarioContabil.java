package br.com.webpublico.entidadesauxiliares.contabil;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class ResumoIntegracaoTributarioContabil {
    private BigDecimal valorReceita;
    private BigDecimal valorCreditoReceber;
    private BigDecimal valorDividaAtiva;
    private List<LoteBaixaIntegracaoTributarioContabil> lotes;

    public ResumoIntegracaoTributarioContabil() {
        valorReceita = BigDecimal.ZERO;
        valorCreditoReceber = BigDecimal.ZERO;
        valorDividaAtiva = BigDecimal.ZERO;
        lotes = Lists.newArrayList();
    }

    public List<LoteBaixaIntegracaoTributarioContabil> getLotes() {
        return lotes;
    }

    public void setLotes(List<LoteBaixaIntegracaoTributarioContabil> lotes) {
        this.lotes = lotes;
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

