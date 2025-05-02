package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;


public class PagamentoNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {


    private Long id;

    private CondicaoPagamento tipoPagamento;
    private BigDecimal quantidadeParcelas;
    private BigDecimal valor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CondicaoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(CondicaoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public BigDecimal getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(BigDecimal quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public enum CondicaoPagamento {
        A_VISTA,APRESENTACAO,A_PRAZO,CARTAO_DEBITO,CARTAO_CREDITO
    }

}
