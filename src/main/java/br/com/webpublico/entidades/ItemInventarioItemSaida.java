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
@Etiqueta("Item Inventário Estoque Item Saída Material")
@Table(name = "ITEMINVENTARIOITEMSAIDA")
public class ItemInventarioItemSaida extends SuperEntidade{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Etiqueta("Item Inventário Estoque")
    private ItemInventarioEstoque itemInventarioEstoque;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Etiqueta("Item Saída Material")
    private ItemSaidaMaterial itemSaidaMaterial;

    public ItemInventarioItemSaida() {
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

    public ItemInventarioEstoque getItemInventarioEstoque() {
        return itemInventarioEstoque;
    }

    public void setItemInventarioEstoque(ItemInventarioEstoque itemInventarioEstoque) {
        this.itemInventarioEstoque = itemInventarioEstoque;
    }
}
