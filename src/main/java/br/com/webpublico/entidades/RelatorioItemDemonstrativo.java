/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author juggernaut
 */
@Audited
@Entity
@Etiqueta("Relatório itens Demonstrativo")

public class RelatorioItemDemonstrativo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Invisivel
    @OneToMany(mappedBy = "relatorioItemDemonstrativo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RelatorioGrupoItemDemons> relatorioGrupoItemDemons;
    @ManyToOne
    private Exercicio exercicio;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RelatorioItemDemonstrativo)) {
            return false;
        }
        RelatorioItemDemonstrativo other = (RelatorioItemDemonstrativo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public List<RelatorioGrupoItemDemons> getRelatorioGrupoItemDemons() {
        return relatorioGrupoItemDemons;
    }

    public void setRelatorioGrupoItemDemons(List<RelatorioGrupoItemDemons> relatorioGrupoItemDemons) {
        this.relatorioGrupoItemDemons = relatorioGrupoItemDemons;
    }

    @Override
    public String toString() {
        return this.descricao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }
}
