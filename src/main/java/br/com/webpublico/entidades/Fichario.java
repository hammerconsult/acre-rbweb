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
 * @author andre
 */
@Entity
@Audited
@Etiqueta("Fichário")
@GrupoDiagrama(nome = "RecursosHumanos")
public class Fichario extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @OrderBy
    private Integer codigo;
    @Pesquisavel
    @Etiqueta("Descrição")
    @Obrigatorio
    @Tabelavel
    private String descricao;
    @Etiqueta("Gavetas Fichário")
    @OneToMany(mappedBy = "fichario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("codigo")
    private List<GavetaFichario> gavetasFicharios;

    public Fichario() {
        gavetasFicharios = new ArrayList<GavetaFichario>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<GavetaFichario> getGavetasFicharios() {
        return gavetasFicharios;
    }

    public void setGavetasFicharios(List<GavetaFichario> gavetasFicharios) {
        this.gavetasFicharios = gavetasFicharios;
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
        if (!(object instanceof Fichario)) {
            return false;
        }
        Fichario other = (Fichario) object;
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
