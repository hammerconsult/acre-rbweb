/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.ParecerAnalise;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author AndreGustavo
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RBTRans")
@Etiqueta("Analise do Recurso RBTrans")
public class AnaliseRecursoRBTrans implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date data;
    @Enumerated(EnumType.STRING)
    private ParecerAnalise parecerAnalise;
    private String descricao;

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public ParecerAnalise getParecerAnalise() {
        return parecerAnalise;
    }

    public void setParecerAnalise(ParecerAnalise parecerAnalise) {
        this.parecerAnalise = parecerAnalise;
    }

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
        if (!(object instanceof AnaliseRecursoRBTrans)) {
            return false;
        }
        AnaliseRecursoRBTrans other = (AnaliseRecursoRBTrans) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.AnaliseRecursoRBTrans[ id=" + id + " ]";
    }
}
