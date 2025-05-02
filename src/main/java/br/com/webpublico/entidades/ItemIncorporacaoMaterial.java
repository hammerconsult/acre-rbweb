/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Sarruf
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Item Incorporação Material")
public class ItemIncorporacaoMaterial extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @Obrigatorio
    private ItemEntradaMaterial itemEntradaMaterial;

    @Obrigatorio
    @OneToOne
    private Material material;

    public ItemIncorporacaoMaterial() {
        inicializarAtributos();
    }

    public ItemIncorporacaoMaterial(ItemEntradaMaterial item) {
        inicializarAtributos();
        this.itemEntradaMaterial = item;
    }

    private void inicializarAtributos() {
        this.material = new Material();
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

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemIncorporacaoMaterial[ id=" + id + " ]";
    }
}
