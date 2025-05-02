/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.envers.Audited;

/**
 *
 * @author JaimeDias
 */
@Entity
@Audited
@Table(name="HIERARQUIAORGCORRE")
public class HierarquiaOrganizacionalCorrespondente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name="HIERARQUIAORGORC_ID")
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrc;
    @ManyToOne
    @JoinColumn(name="HIERARQUIAORGADM_ID")
    private HierarquiaOrganizacional hierarquiaOrganizacionalAdm;
    @Temporal(TemporalType.DATE)
    private Date dataInicio;
    @Temporal(TemporalType.DATE)
    private Date dataFim;
    @Transient
    private Long criadoEm;

    public HierarquiaOrganizacionalCorrespondente() {
        this.criadoEm = System.nanoTime();
    }


    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrc() {
        return hierarquiaOrganizacionalOrc;
    }

    public void setHierarquiaOrganizacionalOrc(HierarquiaOrganizacional hierarquiaOrganizacionalOrc) {
        this.hierarquiaOrganizacionalOrc = hierarquiaOrganizacionalOrc;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalAdm() {
        return hierarquiaOrganizacionalAdm;
    }

    public void setHierarquiaOrganizacionalAdm(HierarquiaOrganizacional hierarquiaOrganizacionalAdm) {
        this.hierarquiaOrganizacionalAdm = hierarquiaOrganizacionalAdm;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnidadeOrganizacional)) return false;

        HierarquiaOrganizacionalCorrespondente other = (HierarquiaOrganizacionalCorrespondente) o;

        if (id == null || other.id == null) { //se o ID de um dos dois for null
            return this.criadoEm != null ? criadoEm.equals(other.criadoEm) : other.criadoEm != null; //se o criadoEm desse for diferente de nulo, retorna o equals dele
        } else {                                                                                     //senão, retorna o criadoEm do outro igual a nulo também
            return this.id != null ? this.id.equals(other.id) : other.id != null; //igual o criadoEm, mas com id
        }
    }

    @Override
    public int hashCode() {
        if (id == null ) { //se o ID de um dos dois for null
            return this.criadoEm.hashCode() ; //se o criadoEm desse for diferente de nulo, retorna o equals dele
        } else {                                                                                     //senão, retorna o criadoEm do outro igual a nulo também
            return this.id.hashCode();
        }
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
