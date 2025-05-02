/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Table(name = "ITEMENTIDADEUNIDADEORG")
public class ItemEntidadeDPContasUnidadeOrganizacional extends SuperEntidade implements Serializable, Comparable<ItemEntidadeDPContasUnidadeOrganizacional> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ItemEntidadeDPContas itemEntidadeDPContas;
    @ManyToOne
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public ItemEntidadeDPContasUnidadeOrganizacional() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemEntidadeDPContas getItemEntidadeDPContas() {
        return itemEntidadeDPContas;
    }

    public void setItemEntidadeDPContas(ItemEntidadeDPContas itemEntidadeDPContas) {
        this.itemEntidadeDPContas = itemEntidadeDPContas;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public String toString() {
        return "" + hierarquiaOrganizacional;
    }

    @Override
    public int compareTo(ItemEntidadeDPContasUnidadeOrganizacional o) {
        if (this.getHierarquiaOrganizacional() != null && o.getHierarquiaOrganizacional() != null) {
            return this.getHierarquiaOrganizacional().getCodigo().compareTo(o.getHierarquiaOrganizacional().getCodigo());
        }
        return 0;
    }
}
