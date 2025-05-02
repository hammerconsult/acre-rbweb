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
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Andre
 */
@Entity
@Audited

@GrupoDiagrama(nome = "Tributario")
@Table(name = "ITEMPARAMETRORENDAPATR")
public class ItemParametroDescontoRendaPatrimonial implements Comparable<ItemParametroDescontoRendaPatrimonial>, Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "PARAMETRORENDAPATRIMONIAL_ID")
    private ParametroDescontoRendaPatrimonial parametroDescontoRendaPatrimonial;
    @Etiqueta("Sequência")
    private Long sequencia;
    @Etiqueta("Valor Inicial")
    private BigDecimal valorInicial;
    @Etiqueta("Valor Final")
    private BigDecimal valorFinal;
    @Etiqueta("Quantidade de Parcelas")
    private Integer quantidadeParcelas;
    @Etiqueta("Desconto na Dívida")
    private BigDecimal descontoNaDivida;
    @Etiqueta("Desconto na Multa")
    private BigDecimal descontoNaMulta;
    @Etiqueta("Desconto nos Juros")
    private BigDecimal descontoNosJuros;
    @Transient
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public ItemParametroDescontoRendaPatrimonial() {
        dataRegistro = new Date();
        quantidadeParcelas = new Integer(1);
        descontoNaDivida = new BigDecimal(BigInteger.ZERO);
        descontoNaMulta = new BigDecimal(BigInteger.ZERO);
        descontoNosJuros = new BigDecimal(BigInteger.ZERO);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParametroDescontoRendaPatrimonial getParametroDescontoRendaPatrimonial() {
        return parametroDescontoRendaPatrimonial;
    }

    public void setParametroDescontoRendaPatrimonial(ParametroDescontoRendaPatrimonial parametroDescontoRendaPatrimonial) {
        this.parametroDescontoRendaPatrimonial = parametroDescontoRendaPatrimonial;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public Integer getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(Integer quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public BigDecimal getDescontoNaDivida() {
        return descontoNaDivida;
    }

    public void setDescontoNaDivida(BigDecimal descontoNaDivida) {
        this.descontoNaDivida = descontoNaDivida;
    }

    public BigDecimal getDescontoNaMulta() {
        return descontoNaMulta;
    }

    public void setDescontoNaMulta(BigDecimal descontoNaMulta) {
        this.descontoNaMulta = descontoNaMulta;
    }

    public BigDecimal getDescontoNosJuros() {
        return descontoNosJuros;
    }

    public void setDescontoNosJuros(BigDecimal descontoNosJuros) {
        this.descontoNosJuros = descontoNosJuros;
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
        if (!(object instanceof ItemParametroDescontoRendaPatrimonial)) {
            return false;
        }
        ItemParametroDescontoRendaPatrimonial other = (ItemParametroDescontoRendaPatrimonial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)) || (this.dataRegistro == null && other.dataRegistro != null) || (this.dataRegistro != null && !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }


    public boolean equalsID(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemParametroDescontoRendaPatrimonial)) {
            return false;
        }
        ItemParametroDescontoRendaPatrimonial other = (ItemParametroDescontoRendaPatrimonial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return sequencia.toString();
    }

    @Override
    public int compareTo(ItemParametroDescontoRendaPatrimonial i) {
        int var = this.sequencia.compareTo(i.getSequencia());
        if (var == 0) {
            return this.sequencia.compareTo(i.getSequencia());
        } else {
            return var;
        }
    }
}
