/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "PPA")
@Etiqueta("Cota Orçamentária")
public class GrupoCotaORC implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Grupo Orçamentário")
    @ManyToOne
    private GrupoOrcamentario grupoOrcamentario;
    @Invisivel
    @Tabelavel(campoSelecionado = false)
    @OneToMany(mappedBy = "grupoCotaORC", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CotaOrcamentaria> cotaOrcamentaria;

    public GrupoCotaORC() {
        cotaOrcamentaria = new ArrayList<CotaOrcamentaria>();
    }

    public GrupoCotaORC(GrupoOrcamentario grupoOrcamentario, List<CotaOrcamentaria> cotaOrcamentaria) {
        this.grupoOrcamentario = grupoOrcamentario;
        this.cotaOrcamentaria = cotaOrcamentaria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CotaOrcamentaria> getCotaOrcamentaria() {
        return cotaOrcamentaria;
    }

    public void setCotaOrcamentaria(List<CotaOrcamentaria> cotaOrcamentaria) {
        this.cotaOrcamentaria = cotaOrcamentaria;
    }

    public GrupoOrcamentario getGrupoOrcamentario() {
        return grupoOrcamentario;
    }

    public void setGrupoOrcamentario(GrupoOrcamentario grupoOrcamentario) {
        this.grupoOrcamentario = grupoOrcamentario;
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
        if (!(object instanceof GrupoCotaORC)) {
            return false;
        }
        GrupoCotaORC other = (GrupoCotaORC) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.GrupoCotaORC[ id=" + id + " ]";
    }
}
