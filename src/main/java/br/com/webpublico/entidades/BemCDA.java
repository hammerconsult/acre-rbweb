/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.envers.Audited;

/**
 *
 * @author Arthur
 */
@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BemCDA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dataPenhora;
    @Temporal(TemporalType.DATE)
    private Date dataAvaliacao;
    private double valorAvaliacao;
    private int codigoCategoria;
    private String descricaoCategoria;
    private String descricaoBem;
    @ManyToOne
    private CertidaoDividaAtiva certidaoDividaAtiva;
    @Transient
    private Long criadoEm;

    public BemCDA() {
        this.criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CertidaoDividaAtiva getCertidaoDividaAtiva() {
        return certidaoDividaAtiva;
    }

    public void setCertidaoDividaAtiva(CertidaoDividaAtiva certidaoDividaAtiva) {
        this.certidaoDividaAtiva = certidaoDividaAtiva;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataPenhora() {
        return dataPenhora;
    }

    public void setDataPenhora(Date dataPenhora) {
        this.dataPenhora = dataPenhora;
    }

    public Date getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(Date dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public double getValorAvaliacao() {
        return valorAvaliacao;
    }

    public void setValorAvaliacao(double valorAvaliacao) {
        this.valorAvaliacao = valorAvaliacao;
    }

    public int getCodigoCategoria() {
        return codigoCategoria;
    }

    public void setCodigoCategoria(int codigoCategoria) {
        this.codigoCategoria = codigoCategoria;
    }

    public String getDescricaoCategoria() {
        return descricaoCategoria;
    }

    public void setDescricaoCategoria(String descricaoCategoria) {
        this.descricaoCategoria = descricaoCategoria;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
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
