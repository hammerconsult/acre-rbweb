/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

public class MovimentoEstoqueItemRetiradaMaterial implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private MovimentoEstoque movimentoEstoque;
    //@ManyToOne
//    private ItemRetiradaMaterial itemRetiradaMaterial;

    public MovimentoEstoqueItemRetiradaMaterial() {
    }
//
//    public MovimentoEstoqueItemRetiradaMaterial(MovimentoEstoque movimentoEstoque, ItemRetiradaMaterial itemRetiradaMaterial) {
//        this.movimentoEstoque = movimentoEstoque;
//        this.itemRetiradaMaterial = itemRetiradaMaterial;
//    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
//
//    public ItemRetiradaMaterial getItemRetiradaMaterial() {
//        return itemRetiradaMaterial;
//    }
//
//    public void setItemRetiradaMaterial(ItemRetiradaMaterial itemRetiradaMaterial) {
//        this.itemRetiradaMaterial = itemRetiradaMaterial;
//    }

    public MovimentoEstoque getMovimentoEstoque() {
        return movimentoEstoque;
    }

    public void setMovimentoEstoque(MovimentoEstoque movimentoEstoque) {
        this.movimentoEstoque = movimentoEstoque;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MovimentoEstoqueItemRetiradaMaterial other = (MovimentoEstoqueItemRetiradaMaterial) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
