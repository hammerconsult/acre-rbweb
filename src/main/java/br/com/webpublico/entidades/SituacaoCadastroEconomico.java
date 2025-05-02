/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Gustavo
 */
@Entity

@Audited
@GrupoDiagrama(nome = "CadastroEconomico")
public class SituacaoCadastroEconomico implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private SituacaoCadastralCadastroEconomico situacaoCadastral;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataAlteracao;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public SituacaoCadastralCadastroEconomico getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public void setSituacaoCadastral(SituacaoCadastralCadastroEconomico situacaoCadastral) {
        this.situacaoCadastral = situacaoCadastral;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the dataRegistro fields are not set
        if (!(object instanceof SituacaoCadastroEconomico)) {
            return false;
        }
        SituacaoCadastroEconomico other = (SituacaoCadastroEconomico) object;
        if ((this.dataRegistro == null && other.dataRegistro != null) || (this.dataRegistro != null && !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return situacaoCadastral.getDescricao();
    }

    @Override
    public int compareTo(Object o) {
        if ((o == null) || !(o instanceof SituacaoCadastroEconomico)) {
            return 0;
        } else if (this.dataAlteracao == null || ((SituacaoCadastroEconomico) o).getDataAlteracao() == null) {
            return 0;
        } else {
            return this.dataAlteracao.compareTo(((SituacaoCadastroEconomico) o).getDataAlteracao());
        }
    }
}
