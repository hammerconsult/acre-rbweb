/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

/**
 * Insumo utilizado nos itens produzidos.
 *
 * @author cheles
 */
public class InsumoProducao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Material material;

    private BigDecimal quantidade;

    /**
     * Opcional, de acordo com o atributo Material.controleDeLote
     */
    @ManyToOne
    private LoteMaterial loteMaterial;

    @ManyToOne
    private LocalEstoqueOrcamentario localEstoqueOrcamentario;

    @OneToMany(mappedBy = "insumoProducao")
    private List<InsumoAplicado> insumosAplicados;

    @OneToOne
    private ItemSaidaMaterial itemSaidaMaterial;

    public InsumoProducao() {
        this.localEstoqueOrcamentario = new LocalEstoqueOrcamentario();
    }

    public InsumoProducao(Material material, BigDecimal quantidade, Date dataRegistro, LoteMaterial loteMaterial) {
        this.material = material;
        this.quantidade = quantidade;
        this.criadoEm = System.nanoTime();
        this.loteMaterial = loteMaterial;
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

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public List<InsumoAplicado> getInsumosAplicados() {
        return insumosAplicados;
    }

    public void setInsumosAplicados(List<InsumoAplicado> insumosAplicados) {
        this.insumosAplicados = insumosAplicados;
    }

    public ItemSaidaMaterial getItemSaidaMaterial() {
        return itemSaidaMaterial;
    }

    public void setItemSaidaMaterial(ItemSaidaMaterial itemSaidaMaterial) {
        this.itemSaidaMaterial = itemSaidaMaterial;
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
        return material + " - " + quantidade + " - " + loteMaterial;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoqueOrcamentario.getLocalEstoque();
    }
}
