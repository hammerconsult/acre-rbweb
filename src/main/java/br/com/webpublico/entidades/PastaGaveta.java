/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@Entity
@Audited
public class PastaGaveta extends SuperEntidade implements Comparable<PastaGaveta> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Número da Pasta")
    @OrderBy
    private Integer numero;
    @ManyToOne
    private GavetaFichario gavetaFichario;
    @OneToMany(mappedBy = "pastaGaveta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PastaGavetaContratoFP> pastasGavetasContratosFP;
    @Transient
    private Date dataRegistro;

    public PastaGaveta() {
        dataRegistro = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public GavetaFichario getGavetaFichario() {
        return gavetaFichario;
    }

    public void setGavetaFichario(GavetaFichario gavetaFichario) {
        this.gavetaFichario = gavetaFichario;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public List<PastaGavetaContratoFP> getPastasContratosFP() {
        return pastasGavetasContratosFP;
    }

    public void setPastasContratosFP(List<PastaGavetaContratoFP> pastasContratosFP) {
        this.pastasGavetasContratosFP = pastasContratosFP;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PastaGaveta other = (PastaGaveta) obj;
        if ((this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro)))
            || ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))) {
            return false;
        }
        return true;
    }


    public boolean equalsID(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PastaGaveta)) {
            return false;
        }
        PastaGaveta other = (PastaGaveta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Pasta " + numero;
    }

    @Override
    public int compareTo(PastaGaveta o) {
        return o.numero.compareTo(this.numero);
    }
}
