/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author gustavo
 */
@Entity

@Audited
public class ItemConfiguracaoAcrescimos implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer qntDias;
    private BigDecimal valor;
    @ManyToOne
    private MultaConfiguracaoAcrescimo multa;
    @Transient
    private Long criadoEm;

    public ItemConfiguracaoAcrescimos() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQntDias() {
        return qntDias;
    }

    public void setQntDias(Integer qntDias) {
        this.qntDias = qntDias;
    }

    public BigDecimal getValor() {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public MultaConfiguracaoAcrescimo getConfiguracaoAcrescimos() {
        return multa;
    }

    public void setConfiguracaoAcrescimos(MultaConfiguracaoAcrescimo multa) {
        this.multa = multa;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemConfiguracaoAcrescimos other = (ItemConfiguracaoAcrescimos) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.qntDias != other.qntDias && (this.qntDias == null || !this.qntDias.equals(other.qntDias))) {
            return false;
        }
        if (this.valor != other.valor && (this.valor == null || !this.valor.equals(other.valor))) {
            return false;
        }
        if (this.multa != other.multa && (this.multa == null || !this.multa.equals(other.multa))) {
            return false;
        }
        if (this.criadoEm != other.criadoEm && (this.criadoEm == null || !this.criadoEm.equals(other.criadoEm))) {
            return false;
        }
        return true;
    }

//    @Override
//    public boolean equals(Object object) {
//        return IdentidadeDaEntidade.calcularEquals(this, object);
//    }

    @Override
    public String toString() {
        return "Dias de atraso:" + qntDias + ", valor da multa: " + valor + "%";
    }

    @Override
    public int compareTo(Object o) {
        return qntDias.compareTo(((ItemConfiguracaoAcrescimos) o).getQntDias());
    }
}
