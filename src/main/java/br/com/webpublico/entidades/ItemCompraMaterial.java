/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Item Compra Material")
public class ItemCompraMaterial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @Etiqueta("Item Entrada Material")
    private ItemEntradaMaterial itemEntradaMaterial;

    @OneToOne
    @Etiqueta("Item Requisição de Compra")
    private ItemRequisicaoDeCompra itemRequisicaoDeCompra;

    public ItemCompraMaterial() {
        super();
    }

    ItemCompraMaterial(ItemRequisicaoDeCompra irc, ItemEntradaMaterial iem) {
        this.itemEntradaMaterial = iem;
        this.itemRequisicaoDeCompra = irc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemEntradaMaterial getItemEntradaMaterial() {
        return itemEntradaMaterial;
    }

    public void setItemEntradaMaterial(ItemEntradaMaterial itemEntradaMaterial) {
        this.itemEntradaMaterial = itemEntradaMaterial;
    }

    public ItemRequisicaoDeCompra getItemRequisicaoDeCompra() {
        return itemRequisicaoDeCompra;
    }

    public void setItemRequisicaoDeCompra(ItemRequisicaoDeCompra itemRequisicaoDeCompra) {
        this.itemRequisicaoDeCompra = itemRequisicaoDeCompra;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemCompraMaterial[ id=" + id + " ]";
    }

    public ObjetoCompra getObjetoCompra() {
        return itemRequisicaoDeCompra.getObjetoCompra();
    }
}
