/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class ItemEntidadeDPContas extends SuperEntidade implements Serializable, Comparable<ItemEntidadeDPContas> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EntidadeDPContas entidadeDPContas;
    @ManyToOne
    private Entidade entidade;
    @OneToMany(mappedBy = "itemEntidadeDPContas", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Unidades Organizacionais")
    private List<ItemEntidadeDPContasUnidadeOrganizacional> itemEntidadeUnidadeOrganizacional = Lists.newArrayList();
    private Boolean aposentadoPensionista;
    private Boolean gerarSefip13Dezembro;

    public ItemEntidadeDPContas() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getCriadoEm() {
        return criadoEm;
    }

    @Override
    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<ItemEntidadeDPContasUnidadeOrganizacional> getItemEntidadeUnidadeOrganizacional() {
        return itemEntidadeUnidadeOrganizacional;
    }

    public void setItemEntidadeUnidadeOrganizacional(List<ItemEntidadeDPContasUnidadeOrganizacional> itemEntidadeUnidadeOrganizacional) {
        this.itemEntidadeUnidadeOrganizacional = itemEntidadeUnidadeOrganizacional;
    }

    public EntidadeDPContas getEntidadeDPContas() {
        return entidadeDPContas;
    }

    public void setEntidadeDPContas(EntidadeDPContas entidadeDPContas) {
        this.entidadeDPContas = entidadeDPContas;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Boolean getAposentadoPensionista() {
        if (aposentadoPensionista == null) {
            setAposentadoPensionista(Boolean.FALSE);
        }
        return aposentadoPensionista;
    }

    public void setAposentadoPensionista(Boolean aposentadoPensionista) {
        this.aposentadoPensionista = aposentadoPensionista;
    }

    public Boolean getGerarSefip13Dezembro() {
        return gerarSefip13Dezembro != null ? gerarSefip13Dezembro : false;
    }

    public void setGerarSefip13Dezembro(Boolean gerarSefip13Dezembro) {
        this.gerarSefip13Dezembro = gerarSefip13Dezembro;
    }

    @Override
    public String toString() {
        return "" + entidade;
    }

    public boolean temHierarquiaFilhaAdicionada() {
        for (ItemEntidadeDPContasUnidadeOrganizacional itemUnidadeOrganizacional : itemEntidadeUnidadeOrganizacional) {
            if (itemUnidadeOrganizacional.getHierarquiaOrganizacional().getIndiceDoNo() > 2) {
                return true;
            }
        }
        return false;
    }

    public void limparItensEntidadeUnidadeOrganizacional() {
        itemEntidadeUnidadeOrganizacional.clear();
    }

    @Override
    public int compareTo(ItemEntidadeDPContas o) {
        if (entidade != null && o.getEntidade() != null) {
            return entidade.getNome().compareTo(o.getEntidade().getNome());
        }
        return 0;
    }
}
