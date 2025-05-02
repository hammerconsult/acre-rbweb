/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author andre
 */
@Entity

@GrupoDiagrama(nome = "RecursosHumanos")
public class AtestadoDoAfastamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Afastamento")
    @ManyToOne
    private Afastamento afastamento;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Atestado Médico")
    @ManyToOne
    private Medico atestadoMedico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Afastamento getAfastamento() {
        return afastamento;
    }

    public void setAfastamento(Afastamento afastamento) {
        this.afastamento = afastamento;
    }

    public Medico getAtestadoMedico() {
        return atestadoMedico;
    }

    public void setAtestadoMedico(Medico atestadoMedico) {
        this.atestadoMedico = atestadoMedico;
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
        if (!(object instanceof AtestadoDoAfastamento)) {
            return false;
        }
        AtestadoDoAfastamento other = (AtestadoDoAfastamento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return afastamento.toString() + " - " + atestadoMedico.toString();
    }
}
