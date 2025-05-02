/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Tabelavel;
import java.io.Serializable;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.envers.Audited;

/**
 *
 * @author reidocrime
 */
@Audited
@Entity

public class DeParaItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    private String codigoAdmAntiga;
    @Tabelavel
    @ManyToOne
    private UnidadeOrganizacional unidadeAdmAntiga;
    @Tabelavel
    private String codigoAdmNova;
    @Tabelavel
    @ManyToOne
    private UnidadeOrganizacional unidadeAdmNova;
    @Tabelavel
    private String codigoOrcAntiga;
    @ManyToOne
    @Tabelavel
    private UnidadeOrganizacional unidadeOrcAntiga;
    @ManyToOne
    @Tabelavel
    private DePara dePara;

    public DeParaItem(String codigoAdmAntiga, UnidadeOrganizacional unidadeAdmAntiga, String codigoAdmNova, UnidadeOrganizacional unidadeAdmNova, String codigoOrcAntiga, UnidadeOrganizacional unidadeOrcAntiga, DePara dePara) {
        this.codigoAdmAntiga = codigoAdmAntiga;
        this.unidadeAdmAntiga = unidadeAdmAntiga;
        this.codigoAdmNova = codigoAdmNova;
        this.unidadeAdmNova = unidadeAdmNova;
        this.codigoOrcAntiga = codigoOrcAntiga;
        this.unidadeOrcAntiga = unidadeOrcAntiga;
        this.dePara = dePara;
    }

    public DeParaItem() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoAdmAntiga() {
        return codigoAdmAntiga;
    }

    public void setCodigoAdmAntiga(String codigoAdmAntiga) {
        this.codigoAdmAntiga = codigoAdmAntiga;
    }

    public String getCodigoAdmNova() {
        return codigoAdmNova;
    }

    public void setCodigoAdmNova(String codigoAdmNova) {
        this.codigoAdmNova = codigoAdmNova;
    }

    public String getCodigoOrcAntiga() {
        return codigoOrcAntiga;
    }

    public void setCodigoOrcAntiga(String codigoOrcAntiga) {
        this.codigoOrcAntiga = codigoOrcAntiga;
    }

    public UnidadeOrganizacional getUnidadeAdmAntiga() {
        return unidadeAdmAntiga;
    }

    public void setUnidadeAdmAntiga(UnidadeOrganizacional unidadeAdmAntiga) {
        this.unidadeAdmAntiga = unidadeAdmAntiga;
    }

    public UnidadeOrganizacional getUnidadeAdmNova() {
        return unidadeAdmNova;
    }

    public void setUnidadeAdmNova(UnidadeOrganizacional unidadeAdmNova) {
        this.unidadeAdmNova = unidadeAdmNova;
    }

    public UnidadeOrganizacional getUnidadeOrcAntiga() {
        return unidadeOrcAntiga;
    }

    public void setUnidadeOrcAntiga(UnidadeOrganizacional unidadeOrcAntiga) {
        this.unidadeOrcAntiga = unidadeOrcAntiga;
    }

    public DePara getDePara() {
        return dePara;
    }

    public void setDePara(DePara dePara) {
        this.dePara = dePara;
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
        if (!(object instanceof DeParaItem)) {
            return false;
        }
        DeParaItem other = (DeParaItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.DeParaItem[ id=" + id + " ]";
    }
}
