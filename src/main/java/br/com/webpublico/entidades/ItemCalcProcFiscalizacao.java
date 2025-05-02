/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Leonardo
 */
@Entity
@Audited

@GrupoDiagrama(nome = "fiscalizacaogeral")
@Etiqueta("Item do Calculo do Processo de Fiscalizacao Geral")
public class ItemCalcProcFiscalizacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoProcFiscalizacao calculo;
    @ManyToOne
    private ItemProcessoFiscalizacao itemProcessoFiscalizacao;
    private BigDecimal valorReal;
    private BigDecimal valorEfetivo;
    @Transient
    private Long criadoEm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculoProcFiscalizacao getCalculo() {
        return calculo;
    }

    public void setCalculo(CalculoProcFiscalizacao calculo) {
        this.calculo = calculo;
    }

    public BigDecimal getValorEfetivo() {
        return valorEfetivo;
    }

    public void setValorEfetivo(BigDecimal valorEfetivo) {
        this.valorEfetivo = valorEfetivo;
    }

    public BigDecimal getValorReal() {
        return valorReal;
    }

    public void setValorReal(BigDecimal valorReal) {
        this.valorReal = valorReal;
    }

    public ItemProcessoFiscalizacao getItemProcessoFiscalizacao() {
        return itemProcessoFiscalizacao;
    }

    public void setItemProcessoFiscalizacao(ItemProcessoFiscalizacao itemProcessoFiscalizacao) {
        this.itemProcessoFiscalizacao = itemProcessoFiscalizacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemCalculoProcFiscalizacao[ id=" + id + " ]";
    }
}
