/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Sarruf
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Entrada de Materiais por Inventário")
@Table(name = "ENTRADAINVENTARIO")
public class EntradaInventarioMaterial extends EntradaMaterial {

    @ManyToOne
    private InventarioEstoque inventarioEstoque;

    public InventarioEstoque getInventarioEstoque() {
        return inventarioEstoque;
    }

    public void setInventarioEstoque(InventarioEstoque inventarioEstoque) {
        this.inventarioEstoque = inventarioEstoque;
    }
}
