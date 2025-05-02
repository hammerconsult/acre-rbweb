/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author cheles
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Item Requisição Saída Material")
@Table(name = "ITEMREQSAIDAMAT")
public class ItemRequisicaoSaidaMaterial extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private ItemSaidaMaterial itemSaidaMaterial;

    @ManyToOne
    private ItemRequisicaoMaterial itemRequisicaoMaterial;

    public ItemRequisicaoSaidaMaterial() {
    }

    public ItemRequisicaoSaidaMaterial(ItemRequisicaoMaterial irm, ItemSaidaMaterial ism) {
        this.itemRequisicaoMaterial = irm;
        this.itemSaidaMaterial = ism;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemRequisicaoMaterial getItemRequisicaoMaterial() {
        return itemRequisicaoMaterial;
    }

    public void setItemRequisicaoMaterial(ItemRequisicaoMaterial itemRequisicaoMaterial) {
        this.itemRequisicaoMaterial = itemRequisicaoMaterial;
    }

    public ItemSaidaMaterial getItemSaidaMaterial() {
        return itemSaidaMaterial;
    }

    public void setItemSaidaMaterial(ItemSaidaMaterial itemSaidaMaterial) {
        this.itemSaidaMaterial = itemSaidaMaterial;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemRequisicaoSaidaMaterial[ id=" + id + " ]";
    }
}
