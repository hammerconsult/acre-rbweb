package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class ItemPlanoContasInternoSearchNfseDTO implements NfseDTO {

    private String cosif;
    private String conta;
    private String descricao;
    private BigDecimal valorServico;
    private BigDecimal baseCalculo;
    private BigDecimal issCalculado;

    public ItemPlanoContasInternoSearchNfseDTO() {
    }

    public String getCosif() {
        return cosif;
    }

    public void setCosif(String cosif) {
        this.cosif = cosif;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getIssCalculado() {
        return issCalculado;
    }

    public void setIssCalculado(BigDecimal issCalculado) {
        this.issCalculado = issCalculado;
    }
}
