/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoItemSindicato;
import br.com.webpublico.enums.TipoValorItemSindicato;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Item do Sindicato")
public class ItemSindicato implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Início da Vigência")
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Final da Vigência")
    private Date finalVigencia;
    @Etiqueta("Valor")
    private BigDecimal valor;
    @Obrigatorio
    @Etiqueta("Tipo Item Sindicato")
    @Enumerated(EnumType.STRING)
    private TipoItemSindicato tipoItemSindicato;
    @Obrigatorio
    @Etiqueta("Tipo Item Sindicato")
    @Enumerated(EnumType.STRING)
    private TipoValorItemSindicato tipoValorItemSindicato;
    @Obrigatorio
    @ManyToOne
    private Sindicato sindicato;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Transient
    private Date dataRegistro;

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

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Sindicato getSindicato() {
        return sindicato;
    }

    public void setSindicato(Sindicato sindicato) {
        this.sindicato = sindicato;
    }

    public TipoItemSindicato getTipoItemSindicato() {
        return tipoItemSindicato;
    }

    public void setTipoItemSindicato(TipoItemSindicato tipoItemSindicato) {
        this.tipoItemSindicato = tipoItemSindicato;
    }

    public TipoValorItemSindicato getTipoValorItemSindicato() {
        return tipoValorItemSindicato;
    }

    public void setTipoValorItemSindicato(TipoValorItemSindicato tipoValorItemSindicato) {
        this.tipoValorItemSindicato = tipoValorItemSindicato;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
        if (!(object instanceof ItemSindicato)) {
            return false;
        }
        ItemSindicato other = (ItemSindicato) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)) || (this.dataRegistro == null && other.dataRegistro != null) || (this.dataRegistro != null && !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return inicioVigencia + " - " + finalVigencia + " - " + tipoItemSindicato.getDescricao() + " - " + tipoItemSindicato.getDescricao() + " - " + valor;
    }

}
