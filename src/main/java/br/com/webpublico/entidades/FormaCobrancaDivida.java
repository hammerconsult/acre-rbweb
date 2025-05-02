/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Terminal-2
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Tributario")
public class FormaCobrancaDivida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FormaCobranca formaCobranca;
    @ManyToOne
    private Divida divida;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public FormaCobranca getFormaCobranca() {
        return formaCobranca;
    }

    public void setFormaCobranca(FormaCobranca formaCobranca) {
        this.formaCobranca = formaCobranca;
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
        if (!(object instanceof FormaCobrancaDivida)) {
            return false;
        }
        FormaCobrancaDivida other = (FormaCobrancaDivida) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String retorno = "";
        if (formaCobranca != null && formaCobranca.getDescricao() != null) {
            retorno += formaCobranca.getDescricao();
        } else {
            retorno += "Sem descrição";
        }
        return retorno;
    }
}
