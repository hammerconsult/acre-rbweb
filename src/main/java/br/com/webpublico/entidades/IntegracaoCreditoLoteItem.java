/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoCreditoReceber;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Leonardo
 */

@Audited
@Entity
public class IntegracaoCreditoLoteItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private SituacaoParcelaValorDivida situacaoParcelaValorDivida;
    @ManyToOne
    private Tributo tributo;
    @OneToOne
    private ItemParcelaValorDivida itemParcelaValorDivida;
    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne
    private ContaReceita contaReceita;
    @Enumerated(EnumType.STRING)
    private OperacaoCreditoReceber operacaoCreditoReceber;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private IntegracaoCreditoLote lote;
    private BigDecimal valorLancado;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLancamento;
    @Transient
    private Long criadoEm;

    public IntegracaoCreditoLoteItem() {
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

    public ItemParcelaValorDivida getItemParcelaValorDivida() {
        return itemParcelaValorDivida;
    }

    public void setItemParcelaValorDivida(ItemParcelaValorDivida itemParcelaValorDivida) {
        this.itemParcelaValorDivida = itemParcelaValorDivida;
    }

    public IntegracaoCreditoLote getLote() {
        return lote;
    }

    public void setLote(IntegracaoCreditoLote lote) {
        this.lote = lote;
    }

    public OperacaoCreditoReceber getOperacaoCreditoReceber() {
        return operacaoCreditoReceber;
    }

    public void setOperacaoCreditoReceber(OperacaoCreditoReceber operacaoCreditoReceber) {
        this.operacaoCreditoReceber = operacaoCreditoReceber;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public SituacaoParcelaValorDivida getSituacaoParcelaValorDivida() {
        return situacaoParcelaValorDivida;
    }

    public void setSituacaoParcelaValorDivida(SituacaoParcelaValorDivida situacaoParcelaValorDivida) {
        this.situacaoParcelaValorDivida = situacaoParcelaValorDivida;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getValorLancado() {
        return valorLancado;
    }

    public void setValorLancado(BigDecimal valorLancado) {
        this.valorLancado = valorLancado;
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
        return "br.com.webpublico.entidades.IntregracaoCreditoLoteItem[ id=" + id + " ]";
    }
}
