/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.controle.GrupoObjetoCompraControlador;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author brainiac
 */
@GrupoDiagrama(nome = "Administrativo")
@Entity

@Audited
@Table(name = "CONTRATOVP")
public class ContratoVeiculoDePublicacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    private VeiculoDePublicacao veiculoDePublicacao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Início de Contratação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioContratacao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Final de Contratação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalContratacao;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    Entidade contratante;
    @Transient
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public ContratoVeiculoDePublicacao() {
        dataRegistro = new Date();
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Entidade getContratante() {
        return contratante;
    }

    public void setContratante(Entidade contratante) {
        this.contratante = contratante;
    }

    public Date getFinalContratacao() {
        return finalContratacao;
    }

    public void setFinalContratacao(Date finalContratacao) {
        this.finalContratacao = finalContratacao;
    }

    public Date getInicioContratacao() {
        return inicioContratacao;
    }

    public void setInicioContratacao(Date inicioContratacao) {
        this.inicioContratacao = inicioContratacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VeiculoDePublicacao getVeiculoDePublicacao() {
        return veiculoDePublicacao;
    }

    public void setVeiculoDePublicacao(VeiculoDePublicacao veiculoDePublicacao) {
        this.veiculoDePublicacao = veiculoDePublicacao;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (this.getId() == null) {
            if (!this.getDataRegistro().equals(((ContratoVeiculoDePublicacao) obj).getDataRegistro())) {
                return false;
            }
        } else {
            if (!this.getId().equals(((ContratoVeiculoDePublicacao) obj).getId())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (this.getId() == null) {
            int hash = 7;
            hash = 67 * hash + (this.dataRegistro != null ? this.dataRegistro.hashCode() : 0);
            return hash;
        } else {
            int hash = 3;
            hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }
    }

    @Override
    public String toString() {
        return "contratante:" + contratante;
    }
}
