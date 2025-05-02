/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "Materiais")
@Entity

@Audited
@Table(name = "MovimentoItemInventario")
public class MovimentoEstoqueItemInventario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Tabelavel
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    private MovimentoEstoque movimentoEstoque;
    @ManyToOne
    private ItemInventarioEstoque itemInventarioEstoque;

    public MovimentoEstoqueItemInventario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemInventarioEstoque getItemInventarioEstoque() {
        return itemInventarioEstoque;
    }

    public void setItemInventarioEstoque(ItemInventarioEstoque itemInventarioEstoque) {
        this.itemInventarioEstoque = itemInventarioEstoque;
    }

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
        final MovimentoEstoqueItemInventario other = (MovimentoEstoqueItemInventario) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return id + "";
    }
}
