package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class NFSAvulsaItemNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {
    private Long id;
    private Integer quantidade;
    private String unidade;
    private String descricao;
    private BigDecimal valorUnitario;
    private BigDecimal aliquotaIss;
    private BigDecimal valorIss;
    private BigDecimal valorTotal;
    private ServicoNfseDTO servico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getAliquotaIss() {
        return aliquotaIss;
    }

    public void setAliquotaIss(BigDecimal aliquotaIss) {
        this.aliquotaIss = aliquotaIss;
    }

    public BigDecimal getValorIss() {
        return valorIss;
    }

    public void setValorIss(BigDecimal valorIss) {
        this.valorIss = valorIss;
    }

    public ServicoNfseDTO getServico() {
        return servico;
    }

    public void setServico(ServicoNfseDTO servico) {
        this.servico = servico;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
