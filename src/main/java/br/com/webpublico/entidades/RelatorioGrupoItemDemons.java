/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author juggernaut
 */
@Entity

@Audited
public class RelatorioGrupoItemDemons implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    private Integer sequencia;
    @ManyToOne
    private RelatorioItemDemonstrativo relatorioItemDemonstrativo;
    @ManyToOne
    private ItemDemonstrativo itemDemonstrativo;
    @Transient
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private String descricao;
    private Integer nivelLinha;

    public RelatorioGrupoItemDemons() {
        this.dataRegistro = new Date();
    }

    public RelatorioGrupoItemDemons(Integer sequencia, RelatorioItemDemonstrativo relatorioItemDemonstrativo, ItemDemonstrativo itemDemonstrativo) {
        this.sequencia = sequencia;
        this.relatorioItemDemonstrativo = relatorioItemDemonstrativo;
        this.itemDemonstrativo = itemDemonstrativo;
        this.dataRegistro = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemDemonstrativo getItemDemonstrativo() {
        return itemDemonstrativo;
    }

    public void setItemDemonstrativo(ItemDemonstrativo itemDemonstrativo) {
        this.itemDemonstrativo = itemDemonstrativo;
    }

    public RelatorioItemDemonstrativo getRelatorioItemDemonstrativo() {
        return relatorioItemDemonstrativo;
    }

    public void setRelatorioItemDemonstrativo(RelatorioItemDemonstrativo relatorioItemDemonstrativo) {
        this.relatorioItemDemonstrativo = relatorioItemDemonstrativo;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dataRegistro != null ? dataRegistro.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RelatorioGrupoItemDemons)) {
            return false;
        }
        RelatorioGrupoItemDemons other = (RelatorioGrupoItemDemons) object;
        if ((this.dataRegistro == null && other.dataRegistro != null) || (this.dataRegistro != null && !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.relatorioItemDemonstrativo.getDescricao() + " - " + this.itemDemonstrativo.getDescricao();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getNivelLinha() {
        return nivelLinha;
    }

    public void setNivelLinha(Integer nivelLinha) {
        this.nivelLinha = nivelLinha;
    }
}
