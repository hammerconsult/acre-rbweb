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
import java.util.Date;
import java.util.List;

/**
 * @author Leonardo
 */

@Audited
@Entity
public class IntegracaoCreditoLote implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lote", orphanRemoval = true)
    private List<IntegracaoCreditoLoteItem> integracaoCreditoLoteItem;
    @ManyToOne
    private CreditoReceber creditoReceber;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private ContaReceita contaReceita;
    @ManyToOne
    private Pessoa pessoa;
    private BigDecimal valorTotal;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLancamento;
    @Transient
    private Long criadoEm;

    public IntegracaoCreditoLote() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public CreditoReceber getCreditoReceber() {
        return creditoReceber;
    }

    public void setCreditoReceber(CreditoReceber creditoReceber) {
        this.creditoReceber = creditoReceber;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public List<IntegracaoCreditoLoteItem> getIntegracaoCreditoLoteItem() {
        return integracaoCreditoLoteItem;
    }

    public void setIntegracaoCreditoLoteItem(List<IntegracaoCreditoLoteItem> integracaoCreditoLoteItem) {
        this.integracaoCreditoLoteItem = integracaoCreditoLoteItem;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
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
        return "br.com.webpublico.entidades.IntegracaoCreditoLote[ id=" + id + " ]";
    }
}
