/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import org.hibernate.envers.Audited;

/**
 *
 * @author reidocrime
 */
@Audited
@Entity

public class DePara implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Tabelavel
    @Etiqueta("Id")
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;
    @OneToMany(mappedBy = "dePara",cascade= CascadeType.ALL,orphanRemoval=true)
    private List<DeParaItem> deParaItems;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataRegistro;

    public DePara() {
        this.dataRegistro = new Date();
        this.deParaItems = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<DeParaItem> getDeParaItems() {
        return deParaItems;
    }

    public void setDeParaItems(List<DeParaItem> deParaItems) {
        this.deParaItems = deParaItems;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DePara)) {
            return false;
        }
        DePara other = (DePara) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.DePara[ id=" + id + " ]";
    }
}
