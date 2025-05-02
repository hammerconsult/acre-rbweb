package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Desenvolvimento on 02/02/2017.
 */
@Entity
@Audited
public class ItemEntradaLevantamento extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private ItemEntradaMaterial itemEntradaMaterial;

    @OneToOne
    private ItemLevantamentoEstoque itemLevantamentoEstoque;

    public ItemEntradaLevantamento() {
        super();
    }

    public ItemEntradaLevantamento(ItemEntradaMaterial itemEntradaMaterial, ItemLevantamentoEstoque itemLevantamentoEstoque) {
        super();
        this.itemEntradaMaterial = itemEntradaMaterial;
        this.itemLevantamentoEstoque = itemLevantamentoEstoque;
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

    public ItemLevantamentoEstoque getItemLevantamentoEstoque() {
        return itemLevantamentoEstoque;
    }

    public void setItemLevantamentoEstoque(ItemLevantamentoEstoque itemLevantamentoEstoque) {
        this.itemLevantamentoEstoque = itemLevantamentoEstoque;
    }
}
