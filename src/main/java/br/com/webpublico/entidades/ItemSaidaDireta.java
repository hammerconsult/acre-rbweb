package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 17/04/14
 * Time: 15:13
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Item da Saída Direta")
public class ItemSaidaDireta extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private ItemSaidaMaterial itemSaidaMaterial;

    @ManyToOne
    private Material material;

    public ItemSaidaDireta() {
    }

    public ItemSaidaDireta(Material material, ItemSaidaMaterial itemSaidaMaterial) {
        this.material = material;
        this.itemSaidaMaterial = itemSaidaMaterial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemSaidaMaterial getItemSaidaMaterial() {
        return itemSaidaMaterial;
    }

    public void setItemSaidaMaterial(ItemSaidaMaterial itemSaidaMaterial) {
        this.itemSaidaMaterial = itemSaidaMaterial;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public String toString() {
        return material.getDescricao();
    }
}
