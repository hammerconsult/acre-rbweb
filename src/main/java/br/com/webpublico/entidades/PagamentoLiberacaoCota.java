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
@Entity

@Audited
@GrupoDiagrama(nome = "Orcamentario")
public class PagamentoLiberacaoCota implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pagamento pagamento;
    @ManyToOne
    private LiberacaoCotaFinanceira liberacaoCotaFinanceira;
    @Transient
    private Long criadoEm;

    public PagamentoLiberacaoCota() {
        criadoEm = System.nanoTime();
    }

    public PagamentoLiberacaoCota(Pagamento pagamento, LiberacaoCotaFinanceira liberacaoCotaFinanceira) {
        this.pagamento = pagamento;
        this.liberacaoCotaFinanceira = liberacaoCotaFinanceira;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LiberacaoCotaFinanceira getLiberacaoCotaFinanceira() {
        return liberacaoCotaFinanceira;
    }

    public void setLiberacaoCotaFinanceira(LiberacaoCotaFinanceira liberacaoCotaFinanceira) {
        this.liberacaoCotaFinanceira = liberacaoCotaFinanceira;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
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
        return "br.com.webpublico.entidades.PagamentoLiberacaoCota[ id=" + id + " ]";
    }
}
