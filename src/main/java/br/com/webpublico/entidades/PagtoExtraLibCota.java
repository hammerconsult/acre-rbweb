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

/**
 * @author venon
 */
@Audited
@GrupoDiagrama(nome = "Orcamentario")
@Entity

public class PagtoExtraLibCota implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PagamentoExtra pagamentoExtra;
    @ManyToOne
    private LiberacaoCotaFinanceira liberacaoCotaFinanceira;
    @Transient
    private Long criadoEm;

    public PagtoExtraLibCota() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LiberacaoCotaFinanceira getLiberacaoCotaFinanceira() {
        return liberacaoCotaFinanceira;
    }

    public void setLiberacaoCotaFinanceira(LiberacaoCotaFinanceira liberacaoCotaFinanceira) {
        this.liberacaoCotaFinanceira = liberacaoCotaFinanceira;
    }

    public PagamentoExtra getPagamentoExtra() {
        return pagamentoExtra;
    }

    public void setPagamentoExtra(PagamentoExtra pagamentoExtra) {
        this.pagamentoExtra = pagamentoExtra;
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
        return "br.com.webpublico.entidades.PagtoExtraLibCota[ id=" + id + " ]";
    }
}
