package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;
import java.util.Date;

public class RelatorioCupomParticipanteDTO {
    private String descricao;
    private String premio;
    private BigDecimal valorPremio;
    private BigDecimal valorBilhete;
    private String pessoa;
    private Date inicio;
    private Date fim;
    private Date dataSorteio;
    private String premiado;
    private BigDecimal numeroBilhete;

    public RelatorioCupomParticipanteDTO() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPremio() {
        return premio;
    }

    public void setPremio(String premio) {
        this.premio = premio;
    }

    public BigDecimal getValorPremio() {
        return valorPremio;
    }

    public void setValorPremio(BigDecimal valorPremio) {
        this.valorPremio = valorPremio;
    }

    public BigDecimal getValorBilhete() {
        return valorBilhete;
    }

    public void setValorBilhete(BigDecimal valorBilhete) {
        this.valorBilhete = valorBilhete;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public String getPremiado() {
        return premiado;
    }

    public void setPremiado(String premiado) {
        this.premiado = premiado;
    }

    public Date getDataSorteio() {
        return dataSorteio;
    }

    public void setDataSorteio(Date dataSorteio) {
        this.dataSorteio = dataSorteio;
    }

    public BigDecimal getNumeroBilhete() {
        return numeroBilhete;
    }

    public void setNumeroBilhete(BigDecimal numeroBilhete) {
        this.numeroBilhete = numeroBilhete;
    }
}
