/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

/**
 * Vínculo entre os insumos utilizados na produção de determinado produto.
 *
 * @author cheles
 */
public class InsumoAplicado implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ItemProduzido itemProduzido;

    @ManyToOne(cascade = CascadeType.ALL)
    private InsumoProducao insumoProducao;
    @Transient
    @Invisivel
    private Long criadoEm;

    public InsumoAplicado() {
        this.criadoEm = System.nanoTime();
    }

    public InsumoAplicado(ItemProduzido itemProduzido, InsumoProducao insumoProducao) {
        this.itemProduzido = itemProduzido;
        this.insumoProducao = insumoProducao;
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InsumoProducao getInsumoProducao() {
        return insumoProducao;
    }

    public void setInsumoProducao(InsumoProducao insumoProducao) {
        this.insumoProducao = insumoProducao;
    }

    public ItemProduzido getItemProduzido() {
        return itemProduzido;
    }

    public void setItemProduzido(ItemProduzido itemProduzido) {
        this.itemProduzido = itemProduzido;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return hashCode() + "";
    }
}
