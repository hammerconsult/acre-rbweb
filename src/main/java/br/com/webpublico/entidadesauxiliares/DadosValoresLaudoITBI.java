package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;

public class DadosValoresLaudoITBI implements Serializable {
    private String tipoNegociacao;
    private BigDecimal aliquota;
    private BigDecimal valorNegociacao;
    private BigDecimal valorITBI;
    private Integer numeroTransmissao;
    private Boolean isento;

    public DadosValoresLaudoITBI() {
        this.aliquota = BigDecimal.ZERO;
        this.valorNegociacao = BigDecimal.ZERO;
        this.valorITBI = BigDecimal.ZERO;
        this.isento = Boolean.FALSE;
    }

    public String getTipoNegociacao() {
        return tipoNegociacao;
    }

    public void setTipoNegociacao(String tipoNegociacao) {
        this.tipoNegociacao = tipoNegociacao;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getValorNegociacao() {
        return valorNegociacao;
    }

    public void setValorNegociacao(BigDecimal valorNegociacao) {
        this.valorNegociacao = valorNegociacao;
    }

    public BigDecimal getValorITBI() {
        return valorITBI;
    }

    public void setValorITBI(BigDecimal valorITBI) {
        this.valorITBI = valorITBI;
    }

    public Integer getNumeroTransmissao() {
        return numeroTransmissao;
    }

    public void setNumeroTransmissao(Integer numeroTransmissao) {
        this.numeroTransmissao = numeroTransmissao;
    }

    public Boolean getIsento() {
        return isento;
    }

    public void setIsento(Boolean isento) {
        this.isento = isento;
    }
}
