package br.com.webpublico.nfse.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImpressaoNotaFiscalNfseItemDetalheDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {

    private String descricao;
    private BigDecimal quantidade;
    private BigDecimal valorServico;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }
}
