/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Usuario
 */
@Etiqueta("Grupo Diária")
@GrupoDiagrama(nome = "ReservaDeDotacao")
@Audited
@Entity

public class GrupoDiaria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Código")
    @Tabelavel
    @Pesquisavel
    private String codigo;
    @Obrigatorio
    @Etiqueta("Descrição")
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @OneToMany(mappedBy = "grupoDiaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrupoPessoasDiarias> grupoPessoasDiarias;

    public GrupoDiaria() {
        grupoPessoasDiarias = new ArrayList<GrupoPessoasDiarias>();
    }

    public GrupoDiaria(String codigo, String descricao, List<GrupoPessoasDiarias> grupoPessoasDiarias) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.grupoPessoasDiarias = grupoPessoasDiarias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<GrupoPessoasDiarias> getGrupoPessoasDiarias() {
        return grupoPessoasDiarias;
    }

    public void setGrupoPessoasDiarias(List<GrupoPessoasDiarias> grupoPessoasDiarias) {
        this.grupoPessoasDiarias = grupoPessoasDiarias;
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
        if (!(object instanceof GrupoDiaria)) {
            return false;
        }
        GrupoDiaria other = (GrupoDiaria) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
