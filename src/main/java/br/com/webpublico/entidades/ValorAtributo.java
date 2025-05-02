/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "AtributosDinamicos")
@Entity

@Audited
public class ValorAtributo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    private String valorString;
    private BigDecimal valorDecimal;
    private Long valorInteiro;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date valorData;
    @ManyToOne
    private ValorPossivel valorDiscreto;
    @ManyToOne
    private Atributo atributo;

    public ValorAtributo() {
        super();
    }

    public ValorAtributo(Atributo atributo) {
        this();
        this.atributo = atributo;
    }

    public ValorAtributo(String valorString, BigDecimal valorDecimal, Long valorInteiro, ValorPossivel valorDiscreto, Atributo atributo) {
        this.valorString = valorString;
        this.valorDecimal = valorDecimal;
        this.valorInteiro = valorInteiro;
        this.valorDiscreto = valorDiscreto;
        this.atributo = atributo;
    }

    public Long getId() {
        return id;
    }

    public Date getValorDate() {
        return valorData;
    }

    public void setValorDate(Date valorDate) {
        this.valorData = valorDate;
    }

    public BigDecimal getValorDecimal() {
        return valorDecimal;
    }

    public void setValorDecimal(BigDecimal valorDecimal) {
        this.valorDecimal = valorDecimal;
    }

    public Long getValorInteiro() {
        return valorInteiro;
    }

    public void setValorInteiro(Long valorInteiro) {
        this.valorInteiro = valorInteiro;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Atributo getAtributo() {
        return atributo;
    }

    public void setAtributo(Atributo atributo) {
        this.atributo = atributo;
    }

    public ValorPossivel getValorDiscreto() {
        return valorDiscreto;
    }

    public void setValorDiscreto(ValorPossivel valorDiscreto) {
        this.valorDiscreto = valorDiscreto;
    }

    public String getValorString() {
        return valorString;
    }

    public void setValorString(String valorString) {
        this.valorString = valorString;
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
        if (!(object instanceof ValorAtributo)) {
            return false;
        }
        ValorAtributo other = (ValorAtributo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String retorno = "";
        if (getValorString() != null) {
            retorno = valorString;
        }
        if (getValorDate() != null) {
            retorno = valorData.toString();
        }
        if (getValorDecimal() != null) {
            retorno = retorno + String.valueOf(valorDecimal);
        }
        if (getValorInteiro() != null) {
            retorno = retorno + getValorInteiro();
        }
        if (getValorDiscreto() != null) {
            retorno = retorno + getValorDiscreto().getDescricao();
        }
        return retorno;
    }
}
