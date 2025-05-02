package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 24/11/2014.
 */
public class Lei4320ExecucaoAnexo19 {
    private String descricao;
    private BigDecimal patrimonioSocial;
    private BigDecimal adiantamentoFuturoCapital;
    private BigDecimal reservaCapital;
    private BigDecimal ajusteAvaliacaoPatrimonial;
    private BigDecimal reservaLucros;
    private BigDecimal demaisReservas;
    private BigDecimal resultadosAcumulados;
    private BigDecimal acoesCotasTesouraria;
    private BigDecimal total;

    public Lei4320ExecucaoAnexo19() {
        patrimonioSocial = BigDecimal.ZERO;
        adiantamentoFuturoCapital = BigDecimal.ZERO;
        reservaCapital = BigDecimal.ZERO;
        ajusteAvaliacaoPatrimonial = BigDecimal.ZERO;
        reservaLucros = BigDecimal.ZERO;
        demaisReservas = BigDecimal.ZERO;
        resultadosAcumulados = BigDecimal.ZERO;
        acoesCotasTesouraria = BigDecimal.ZERO;
        total = BigDecimal.ZERO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPatrimonioSocial() {
        return patrimonioSocial;
    }

    public void setPatrimonioSocial(BigDecimal patrimonioSocial) {
        this.patrimonioSocial = patrimonioSocial;
    }

    public BigDecimal getAdiantamentoFuturoCapital() {
        return adiantamentoFuturoCapital;
    }

    public void setAdiantamentoFuturoCapital(BigDecimal adiantamentoFuturoCapital) {
        this.adiantamentoFuturoCapital = adiantamentoFuturoCapital;
    }

    public BigDecimal getReservaCapital() {
        return reservaCapital;
    }

    public void setReservaCapital(BigDecimal reservaCapital) {
        this.reservaCapital = reservaCapital;
    }

    public BigDecimal getAjusteAvaliacaoPatrimonial() {
        return ajusteAvaliacaoPatrimonial;
    }

    public void setAjusteAvaliacaoPatrimonial(BigDecimal ajusteAvaliacaoPatrimonial) {
        this.ajusteAvaliacaoPatrimonial = ajusteAvaliacaoPatrimonial;
    }

    public BigDecimal getReservaLucros() {
        return reservaLucros;
    }

    public void setReservaLucros(BigDecimal reservaLucros) {
        this.reservaLucros = reservaLucros;
    }

    public BigDecimal getDemaisReservas() {
        return demaisReservas;
    }

    public void setDemaisReservas(BigDecimal demaisReservas) {
        this.demaisReservas = demaisReservas;
    }

    public BigDecimal getResultadosAcumulados() {
        return resultadosAcumulados;
    }

    public void setResultadosAcumulados(BigDecimal resultadosAcumulados) {
        this.resultadosAcumulados = resultadosAcumulados;
    }

    public BigDecimal getAcoesCotasTesouraria() {
        return acoesCotasTesouraria;
    }

    public void setAcoesCotasTesouraria(BigDecimal acoesCotasTesouraria) {
        this.acoesCotasTesouraria = acoesCotasTesouraria;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
