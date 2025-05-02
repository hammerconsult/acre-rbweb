/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author munif
 */
@GrupoDiagrama(nome = "Tributario")
@Entity

@Audited
public class DescontoOpcaoPagamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal percentualDescontoAdimplente;
    private BigDecimal percentualDescontoInadimplente;
    @ManyToOne
    private Tributo tributo;
    @ManyToOne
    private OpcaoPagamento opcaoPagamento;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Transient
    private Long criadoEm;

    public DescontoOpcaoPagamento() {
        this.dataRegistro = new Date();
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OpcaoPagamento getOpcaoPagamento() {
        return opcaoPagamento;
    }

    public void setOpcaoPagamento(OpcaoPagamento opcaoPagamento) {
        this.opcaoPagamento = opcaoPagamento;
    }

    public BigDecimal getPercentualDescontoAdimplente() {
        return percentualDescontoAdimplente;
    }

    public void setPercentualDescontoAdimplente(BigDecimal percentualDescontoAdimplente) {
        this.percentualDescontoAdimplente = percentualDescontoAdimplente;
    }

    public BigDecimal getPercentualDescontoInadimplente() {
        return percentualDescontoInadimplente;
    }

    public void setPercentualDescontoInadimplente(BigDecimal percentualDescontoInadimplente) {
        this.percentualDescontoInadimplente = percentualDescontoInadimplente;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString() {
        return tributo.getDescricao() + percentualDescontoAdimplente + "% / " + percentualDescontoInadimplente + "%";
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
