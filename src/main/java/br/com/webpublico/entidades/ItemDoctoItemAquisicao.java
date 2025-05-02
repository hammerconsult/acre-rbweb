package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 16/10/14
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Etiqueta("Item Documento Item Aquisição")
public class ItemDoctoItemAquisicao extends SuperEntidade implements Comparable<ItemDoctoItemAquisicao> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Solicitação de Aquisição")
    private DoctoFiscalSolicitacaoAquisicao doctoFiscalSolicitacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Etiqueta("Item Documento Fiscal Liquidação")
    private ItemDoctoFiscalLiquidacao itemDoctoFiscalLiquidacao;

    @ManyToOne
    @Etiqueta("Item Requisição de Compra")
    private ItemRequisicaoDeCompra itemRequisicaoDeCompra;

    @Etiqueta("Produto Entregue")
    private String produtoEntregue;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Seguradora")
    private Seguradora seguradora;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Garantia")
    private Garantia garantia;

    @Transient
    private Boolean selecionado;
    @Transient
    private BigDecimal quantidadeDisponivel;
    @Transient
    private List<ItemSolicitacaoAquisicao> itensSolicitacao;

    public ItemDoctoItemAquisicao() {
        selecionado = false;
        itensSolicitacao = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemDoctoFiscalLiquidacao getItemDoctoFiscalLiquidacao() {
        return itemDoctoFiscalLiquidacao;
    }

    public void setItemDoctoFiscalLiquidacao(ItemDoctoFiscalLiquidacao itemDoctoFiscalLiquidacao) {
        this.itemDoctoFiscalLiquidacao = itemDoctoFiscalLiquidacao;
    }

    public ItemRequisicaoDeCompra getItemRequisicaoDeCompra() {
        return itemRequisicaoDeCompra;
    }

    public void setItemRequisicaoDeCompra(ItemRequisicaoDeCompra itemRequisicaoDeCompra) {
        this.itemRequisicaoDeCompra = itemRequisicaoDeCompra;
    }

    public String getProdutoEntregue() {
        return produtoEntregue;
    }

    public void setProdutoEntregue(String produtoEntregue) {
        this.produtoEntregue = produtoEntregue;
    }

    public Seguradora getSeguradora() {
        return seguradora;
    }

    public void setSeguradora(Seguradora seguradora) {
        this.seguradora = seguradora;
    }

    public Garantia getGarantia() {
        return garantia;
    }

    public void setGarantia(Garantia garantia) {
        this.garantia = garantia;
    }

    public DoctoFiscalSolicitacaoAquisicao getDoctoFiscalSolicitacao() {
        return doctoFiscalSolicitacao;
    }

    public void setDoctoFiscalSolicitacao(DoctoFiscalSolicitacaoAquisicao doctoFiscalSolicitacao) {
        this.doctoFiscalSolicitacao = doctoFiscalSolicitacao;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public List<ItemSolicitacaoAquisicao> getItensSolicitacao() {
        return itensSolicitacao;
    }

    public void setItensSolicitacao(List<ItemSolicitacaoAquisicao> itensSolicitacao) {
        this.itensSolicitacao = itensSolicitacao;
    }

    @Override
    public int compareTo(ItemDoctoItemAquisicao o) {
        if (getItemRequisicaoDeCompra() !=null && o.getItemRequisicaoDeCompra() !=null) {
            return getItemRequisicaoDeCompra().getNumero()
                .compareTo(o.getItemRequisicaoDeCompra().getNumero());
        }
        return 0;
    }
}

