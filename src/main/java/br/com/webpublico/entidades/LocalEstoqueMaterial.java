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
 * @author Sarruf
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Local Estoque Material")
public class LocalEstoqueMaterial extends SuperEntidade implements Comparable<LocalEstoqueMaterial> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private LocalEstoque localEstoque;

    @ManyToOne
    private Material material;

    public LocalEstoqueMaterial() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public String toString() {
        return " Material.: " + this.getMaterial().getDescricao();
    }

    @Override
    public int compareTo(LocalEstoqueMaterial o) {
        return o.criadoEm.compareTo(this.criadoEm);
    }
}
