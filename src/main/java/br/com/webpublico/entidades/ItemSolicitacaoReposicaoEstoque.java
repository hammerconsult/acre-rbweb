/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Item Solicitação de Reposição de Estoque")
@Table(name = "ITEMSOLREPESTOQUE")
public class ItemSolicitacaoReposicaoEstoque extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private SolicitacaoReposicaoEstoque solicitacaoRepEstoque;

    @ManyToOne
    private Estoque estoque;

    @ManyToOne
    private PoliticaDeEstoque politicaDeEstoque;

    private BigDecimal quantidadeParaComprar;

    private BigDecimal quantidadeProcessoDeCompra;

    @OneToOne
    private ItemSolicitacao itemSolicitacao;

    @OneToOne
    private ItemLoteFormularioCotacao itemLoteFormulario;

    @ManyToOne
    private LocalEstoque localEstoque;

    private BigDecimal estoqueFisico;

    @ManyToOne
    private Material material;

    public ItemSolicitacaoReposicaoEstoque() {
        super();
    }

    public ItemSolicitacaoReposicaoEstoque(SolicitacaoReposicaoEstoque sre, BigDecimal estoqueFisico, Material material, PoliticaDeEstoque pde, BigDecimal qtdePdc, LocalEstoque local) {
        super();
        this.solicitacaoRepEstoque = sre;
        this.estoqueFisico = estoqueFisico;
        this.politicaDeEstoque = pde;
        this.quantidadeProcessoDeCompra = qtdePdc;
        this.quantidadeParaComprar = geraQuantidadeParaComprarSugerida();
        this.localEstoque = local;
        this.material = material;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoReposicaoEstoque getSolicitacaoRepEstoque() {
        return solicitacaoRepEstoque;
    }

    public void setSolicitacaoRepEstoque(SolicitacaoReposicaoEstoque solicitacaoRepEstoque) {
        this.solicitacaoRepEstoque = solicitacaoRepEstoque;
    }

    public Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
    }

    public ItemSolicitacao getItemSolicitacao() {
        return itemSolicitacao;
    }

    public void setItemSolicitacao(ItemSolicitacao itemSolicitacao) {
        this.itemSolicitacao = itemSolicitacao;
    }

    public PoliticaDeEstoque getPoliticaDeEstoque() {
        return politicaDeEstoque;
    }

    public void setPoliticaDeEstoque(PoliticaDeEstoque politicaDeEstoque) {
        this.politicaDeEstoque = politicaDeEstoque;
    }

    public BigDecimal getQuantidadeParaComprar() {
        return quantidadeParaComprar;
    }

    public void setQuantidadeParaComprar(BigDecimal quantidadeParaComprar) {
        this.quantidadeParaComprar = quantidadeParaComprar;
    }

    public BigDecimal getQuantidadeProcessoDeCompra() {
        return quantidadeProcessoDeCompra;
    }

    public void setQuantidadeProcessoDeCompra(BigDecimal quantidadeProcessoDeCompra) {
        this.quantidadeProcessoDeCompra = quantidadeProcessoDeCompra;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public BigDecimal getEstoqueFisico() {
        return estoqueFisico;
    }

    public void setEstoqueFisico(BigDecimal estoqueFisico) {
        this.estoqueFisico = estoqueFisico;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public ItemLoteFormularioCotacao getItemLoteFormulario() {
        return itemLoteFormulario;
    }

    public void setItemLoteFormulario(ItemLoteFormularioCotacao itemLoteFormulario) {
        this.itemLoteFormulario = itemLoteFormulario;
    }

    @Override
    public String toString() {
        return "Item: " + material.toStringAutoComplete() + ",  Qtde = " + quantidadeParaComprar;
    }

    private BigDecimal geraQuantidadeParaComprarSugerida() {
        if (this.politicaDeEstoque.getEstoqueMaximo() != null) {
            return this.politicaDeEstoque.getEstoqueMaximo().subtract(this.quantidadeProcessoDeCompra).subtract(estoqueFisico);
        }

        return this.politicaDeEstoque.getPontoDeReposicao().subtract(this.quantidadeProcessoDeCompra).subtract(estoqueFisico);
    }
}
