package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoItemProcessoDeCompra;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Audited
@Etiqueta(value = "Item Adjudicação e Homologação")
@Table(name = "ITEMSTATUSFORNECEDORLICIT")
public class ItemStatusFornecedorLicitacao extends SuperEntidade implements Comparable<ItemStatusFornecedorLicitacao> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Status Fornecedor Licitação")
    private StatusFornecedorLicitacao statusFornecedorLicitacao;

    @ManyToOne
    private ItemProcessoDeCompra itemProcessoCompra;

    @Enumerated(EnumType.STRING)
    private SituacaoItemProcessoDeCompra situacao;

    private BigDecimal valorUnitario;

    public ItemStatusFornecedorLicitacao() {
        super();
    }

    public ItemStatusFornecedorLicitacao(StatusFornecedorLicitacao statusFornecedorLicitacao, ItemProcessoDeCompra itemProcessoCompra, SituacaoItemProcessoDeCompra situacao) {
        this.statusFornecedorLicitacao = statusFornecedorLicitacao;
        this.itemProcessoCompra = itemProcessoCompra;
        this.situacao = situacao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusFornecedorLicitacao getStatusFornecedorLicitacao() {
        return statusFornecedorLicitacao;
    }

    public void setStatusFornecedorLicitacao(StatusFornecedorLicitacao statusFornecedorLicitacao) {
        this.statusFornecedorLicitacao = statusFornecedorLicitacao;
    }

    public ItemProcessoDeCompra getItemProcessoCompra() {
        return itemProcessoCompra;
    }

    public void setItemProcessoCompra(ItemProcessoDeCompra itemProcessoCompra) {
        this.itemProcessoCompra = itemProcessoCompra;
    }

    public SituacaoItemProcessoDeCompra getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoItemProcessoDeCompra situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    @Override
    public int compareTo(ItemStatusFornecedorLicitacao o) {
        try {
            return ComparisonChain.start().compare(getItemProcessoCompra().getLoteProcessoDeCompra().getNumero(), o.getItemProcessoCompra().getLoteProcessoDeCompra().getNumero())
                .compare(getItemProcessoCompra().getNumero(), o.getItemProcessoCompra().getNumero()).result();
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
