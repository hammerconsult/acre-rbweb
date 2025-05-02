package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class DetalheItemRPS extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal quantidade;
    private BigDecimal valorServico;
    private String descricao;
    @ManyToOne
    private ItemRPS itemRPS;

    public DetalheItemRPS() {
        quantidade = BigDecimal.ZERO;
        valorServico = BigDecimal.ZERO;
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

    public ItemRPS getItemRPS() {
        return itemRPS;
    }

    public void setItemRPS(ItemRPS itemRPS) {
        this.itemRPS = itemRPS;
    }
}
