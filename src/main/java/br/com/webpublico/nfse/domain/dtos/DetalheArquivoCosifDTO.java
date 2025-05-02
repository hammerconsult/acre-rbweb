package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class DetalheArquivoCosifDTO {
    private String sequenciaNumerica;
    private String numeroConta;
    private Boolean recolheImposto;
    private BigDecimal valorServico;
    private BigDecimal valorAliquota;
    private BigDecimal valorISSQN;

    public DetalheArquivoCosifDTO() {
    }

    public String getSequenciaNumerica() {
        return sequenciaNumerica;
    }

    public void setSequenciaNumerica(String sequenciaNumerica) {
        this.sequenciaNumerica = sequenciaNumerica;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public Boolean getRecolheImposto() {
        return recolheImposto;
    }

    public void setRecolheImposto(Boolean recolheImposto) {
        this.recolheImposto = recolheImposto;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public BigDecimal getValorAliquota() {
        return valorAliquota;
    }

    public void setValorAliquota(BigDecimal valorAliquota) {
        this.valorAliquota = valorAliquota;
    }

    public BigDecimal getValorISSQN() {
        return valorISSQN;
    }

    public void setValorISSQN(BigDecimal valorISSQN) {
        this.valorISSQN = valorISSQN;
    }
}
