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
@Etiqueta("Item Inventário Estoque Item Entrada Material")
@Table(name = "ITEMINVENTITEMENTRADA")
public class ItemInventarioItemEntrada extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Etiqueta("Item Inventário Estoque")
    private ItemInventarioEstoque itemInventarioEstoque;

    @Etiqueta("Item Entrada Material")
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ItemEntradaMaterial itemEntradaMaterial;

    public ItemInventarioItemEntrada() {
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

    public ItemInventarioEstoque getItemInventarioEstoque() {
        return itemInventarioEstoque;
    }

    public void setItemInventarioEstoque(ItemInventarioEstoque itemInventarioEstoque) {
        this.itemInventarioEstoque = itemInventarioEstoque;
    }

}
