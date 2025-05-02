/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

/**
 * Uma produção representa um processo fabril, no qual um ou vários
 * produtos resultantes são gerados.
 *
 * @author cheles
 */
public class ItemProduzido extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private BigDecimal quantidade;

    @ManyToOne
    private Producao producao;

    @ManyToOne
    private LoteMaterial loteMaterial;

    @ManyToOne
    private LocalEstoqueOrcamentario localEstoqueOrcamentario;

    @ManyToOne
    private Material material;

    private BigDecimal valor;
    @OneToMany(mappedBy = "itemProduzido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InsumoAplicado> insumosAplicados;

    @OneToOne
    private ItemEntradaMaterial itemEntradaMaterial;

    @Enumerated(EnumType.STRING)
    private TipoEstoque tipoEstoque;

    public ItemProduzido() {
        this.localEstoqueOrcamentario = new LocalEstoqueOrcamentario();
    }

    public ItemProduzido(Producao prod) {
        this.producao = prod;
        this.localEstoqueOrcamentario = new LocalEstoqueOrcamentario();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteMaterial getLoteMaterial() {
        return loteMaterial;
    }

    public void setLoteMaterial(LoteMaterial loteMaterial) {
        this.loteMaterial = loteMaterial;
    }

    public LocalEstoqueOrcamentario getLocalEstoqueOrcamentario() {
        return localEstoqueOrcamentario;
    }

    public void setLocalEstoqueOrcamentario(LocalEstoqueOrcamentario localEstoqueOrcamentario) {
        this.localEstoqueOrcamentario = localEstoqueOrcamentario;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Producao getProducao() {
        return producao;
    }

    public void setProducao(Producao producao) {
        this.producao = producao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<InsumoAplicado> getInsumosAplicados() {
        return insumosAplicados;
    }

    public void setInsumosAplicados(List<InsumoAplicado> insumosAplicados) {
        this.insumosAplicados = insumosAplicados;
    }

    public ItemEntradaMaterial getItemEntradaMaterial() {
        return itemEntradaMaterial;
    }

    public void setItemEntradaMaterial(ItemEntradaMaterial itemEntradaMaterial) {
        this.itemEntradaMaterial = itemEntradaMaterial;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public String identificacaoDoLote() {
        if (this.getLoteMaterial() == null) {
            return "Não requer lote.";
        } else {
            return this.getLoteMaterial().getIdentificacao();
        }
    }

    @Override
    public String toString() {
        return this.material.getDescricao() + "  -  Qtde: " + this.quantidade;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoqueOrcamentario.getLocalEstoque();
    }

    public BigDecimal getValorFinanceiroTotal() {
        return getQuantidade().multiply(getValor());
    }
}
