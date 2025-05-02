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

/**
 * @author andre
 */
@Entity

@Etiqueta("Médico")
@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
public class Medico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Registro CRM")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String registroCRM;

    @Etiqueta("Médico")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private PessoaFisica medico;

    @Obrigatorio
    @ManyToOne
    private UF ufCRM;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisica getMedico() {
        return medico;
    }

    public void setMedico(PessoaFisica medico) {
        this.medico = medico;
    }

    public String getRegistroCRM() {
        return registroCRM;
    }

    public void setRegistroCRM(String registroCRM) {
        this.registroCRM = registroCRM;
    }

    public UF getUfCRM() {
        return ufCRM;
    }

    public void setUfCRM(UF ufCRM) {
        this.ufCRM = ufCRM;
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
        if (!(object instanceof Medico)) {
            return false;
        }
        Medico other = (Medico) object;
        if ((id == null && other.id != null) || (id != null && !id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return medico + " - " + registroCRM;
    }
}
