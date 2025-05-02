package br.com.webpublico.nfse.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DetalheItemDeclaracaoServicoNfseDTO implements NfseDTO {

    private Long id;
    private BigDecimal quantidade;
    private BigDecimal valorServico;
    private String descricao;


    public DetalheItemDeclaracaoServicoNfseDTO() {
    }

    @JsonIgnore
    public static DetalheItemDeclaracaoServicoNfseDTO copy(DetalheItemDeclaracaoServicoNfseDTO itemDeclaracaoServico, DeclaracaoPrestacaoServicoNfseDTO declaracaoPrestacaoServico) {
        DetalheItemDeclaracaoServicoNfseDTO item = new DetalheItemDeclaracaoServicoNfseDTO();
        item.descricao = itemDeclaracaoServico.getDescricao();
        item.quantidade = itemDeclaracaoServico.getQuantidade();
        item.setValorServico(itemDeclaracaoServico.getValorServico());
        return item;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    @Override
    public String toString() {
        return "ItemDeclaracaoServicoNfseDTO{" +
                "id=" + id +
                ", quantidade=" + quantidade +
                ", valorServico=" + valorServico +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
