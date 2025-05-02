package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;
import java.util.Date;

public class NFSAvulsaSearchNfseDTO implements NfseDTO {

    private Long id;
    private Long numero;
    private Date dataNota;
    private String tomador;
    private String situacao;
    private BigDecimal valorServicos;
    private BigDecimal valorTotalIss;
    private BigDecimal valorIss;

    public NFSAvulsaSearchNfseDTO() {
    }

    public NFSAvulsaSearchNfseDTO(Long id, Long numero, Date dataNota, String tomador, String situacao, BigDecimal valorServicos, BigDecimal valorTotalIss, BigDecimal valorIss) {
        this.id = id;
        this.numero = numero;
        this.dataNota = dataNota;
        this.tomador = tomador;
        this.situacao = situacao;
        this.valorServicos = valorServicos;
        this.valorTotalIss = valorTotalIss;
        this.valorIss = valorIss;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataNota() {
        return dataNota;
    }

    public void setDataNota(Date dataNota) {
        this.dataNota = dataNota;
    }

    public String getTomador() {
        return tomador;
    }

    public void setTomador(String tomador) {
        this.tomador = tomador;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getValorServicos() {
        return valorServicos;
    }

    public void setValorServicos(BigDecimal valorServicos) {
        this.valorServicos = valorServicos;
    }

    public BigDecimal getValorTotalIss() {
        return valorTotalIss;
    }

    public void setValorTotalIss(BigDecimal valorTotalIss) {
        this.valorTotalIss = valorTotalIss;
    }

    public BigDecimal getValorIss() {
        return valorIss;
    }

    public void setValorIss(BigDecimal valorIss) {
        this.valorIss = valorIss;
    }
}
