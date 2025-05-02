package br.com.webpublico.nfse.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImpressaoNotaFiscalNfseItemDTO implements NfseDTO {

    private String tributavel;
    private String descricao;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorDesconto;
    private List<ImpressaoNotaFiscalNfseItemDetalheDTO> detalhes;

    public String getTributavel() {
        return tributavel;
    }

    public void setTributavel(String tributavel) {
        this.tributavel = tributavel;
    }

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

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public List<ImpressaoNotaFiscalNfseItemDetalheDTO> getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(List<ImpressaoNotaFiscalNfseItemDetalheDTO> detalhes) {
        this.detalhes = detalhes;
    }
}
