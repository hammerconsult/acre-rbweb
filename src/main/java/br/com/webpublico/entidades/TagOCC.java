/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.EntidadeOCC;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author major
 */
@Entity
@Audited

@Etiqueta("Tag OCC")
public class TagOCC implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel(TIPOCAMPO = Tabelavel.TIPOCAMPO.NUMERO_ORDENAVEL)
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Código")
    private String codigo;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Entidade OCC")
    private EntidadeOCC entidadeOCC;

    public TagOCC() {
    }

    public TagOCC(String codigo, String descricao, EntidadeOCC entidadeOCC) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.entidadeOCC = entidadeOCC;
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

    public EntidadeOCC getEntidadeOCC() {
        return entidadeOCC;
    }

    public void setEntidadeOCC(EntidadeOCC entidadeOCC) {
        this.entidadeOCC = entidadeOCC;
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
        if (!(object instanceof TagOCC)) {
            return false;
        }
        TagOCC other = (TagOCC) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao + " (" + entidadeOCC.getDescricao() + ")";
    }

    public String toStringCodigoDescricao() {
        return codigo + " - " + descricao;
    }
}
