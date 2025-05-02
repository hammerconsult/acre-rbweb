/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoSaidaMaterial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Saída de Materiais por Inventário")
@Table(name = "saidaInventMaterial")
public class SaidaInventarioMaterial extends SaidaMaterial implements Serializable {

    @ManyToOne
    private InventarioEstoque inventarioEstoque;

    public InventarioEstoque getInventarioEstoque() {
        return inventarioEstoque;
    }

    public void setInventarioEstoque(InventarioEstoque inventarioEstoque) {
        this.inventarioEstoque = inventarioEstoque;
    }

    @Override
    public TipoSaidaMaterial getTipoDestaSaida() {
        return TipoSaidaMaterial.INVENTARIO;
    }
}
