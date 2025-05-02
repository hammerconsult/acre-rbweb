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
import java.util.Date;

/**
 * @author andre
 */
@Entity
@Etiqueta("Ato de Pessoal")
@Audited
@Deprecated
@GrupoDiagrama(nome = "RecursosHumanos")
public class AtoDePessoal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Ato Legal")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AtoLegal atoLegal;
    @Tabelavel
    @Etiqueta("Data do Ato")
    @Obrigatorio
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataDoAto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getDataDoAto() {
        return dataDoAto;
    }

    public void setDataDoAto(Date dataDoAto) {
        this.dataDoAto = dataDoAto;
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
        if (!(object instanceof AtoDePessoal)) {
            return false;
        }
        AtoDePessoal other = (AtoDePessoal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return atoLegal + " - " + dataDoAto;
    }
}
