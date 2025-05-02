/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Leonardo
 */
@GrupoDiagrama(nome = "Fiscalizacao")
@Entity
@Audited
public class LancamentoMultaFiscal implements Serializable, Comparable<LancamentoMultaFiscal> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long numeroLancamento;
    @ManyToOne
    private MultaFiscalizacao multaFiscalizacao;
    @Enumerated(value = EnumType.STRING)
    private Mes mes;
    private Integer ano;
    private String descricao;
    private BigDecimal valorMulta;
    private BigDecimal valorMultaIndice;
    private BigDecimal aliquota;
    private BigDecimal baseCalculo;
    private String observacao;
    @Transient
    private Long criadoEm;
    private Integer quantidade;
    @ManyToOne
    private RegistroLancamentoContabil registroLancamentoContabil;
    @ManyToOne
    private LancamentoDoctoFiscal lancamentoDoctoFiscal;
    @ManyToOne
    private LancamentoContabilFiscal lancamentoContabilFiscal;

    public LancamentoMultaFiscal() {
        this.criadoEm = System.nanoTime();
        valorMulta = BigDecimal.ZERO;
        valorMultaIndice = BigDecimal.ZERO;
        aliquota = BigDecimal.ZERO;
        quantidade = 1;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public RegistroLancamentoContabil getRegistroLancamentoContabil() {
        return registroLancamentoContabil;
    }

    public void setRegistroLancamentoContabil(RegistroLancamentoContabil registroLancamentoContabil) {
        this.registroLancamentoContabil = registroLancamentoContabil;
    }

    public LancamentoDoctoFiscal getLancamentoDoctoFiscal() {
        return lancamentoDoctoFiscal;
    }

    public void setLancamentoDoctoFiscal(LancamentoDoctoFiscal lancamentoDoctoFiscal) {
        this.lancamentoDoctoFiscal = lancamentoDoctoFiscal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public MultaFiscalizacao getMultaFiscalizacao() {
        return multaFiscalizacao;
    }

    public void setMultaFiscalizacao(MultaFiscalizacao multaFiscalizacao) {
        this.multaFiscalizacao = multaFiscalizacao;
    }

    public Long getNumeroLancamento() {
        return numeroLancamento;
    }

    public void setNumeroLancamento(Long numeroLancamento) {
        this.numeroLancamento = numeroLancamento;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getValorMultaIndice() {
        return valorMultaIndice;
    }

    public void setValorMultaIndice(BigDecimal valorMultaIndice) {
        this.valorMultaIndice = valorMultaIndice;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public LancamentoContabilFiscal getLancamentoContabilFiscal() {
        return lancamentoContabilFiscal;
    }

    public void setLancamentoContabilFiscal(LancamentoContabilFiscal lancamentoContabilFiscal) {
        this.lancamentoContabilFiscal = lancamentoContabilFiscal;
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
        return "br.com.webpublico.entidades.LancamentoMulta[ id=" + id + " ]";
    }

    @Override
    public int compareTo(LancamentoMultaFiscal o) {
        try {
            int i =  this.getMultaFiscalizacao().getCodigo().compareTo(o.getMultaFiscalizacao().getCodigo());
            if (i == 0) {
                i =  this.getMultaFiscalizacao().getArtigo().compareTo(o.getMultaFiscalizacao().getArtigo());
            }
            if (i == 0) {
                i = this.getAno().compareTo(o.getAno());
            }
            if (i == 0 && this.getMes() != null && o.getMes() != null) {
                i = this.getMes().getNumeroMes().compareTo(o.getMes().getNumeroMes());
            }

            return i;

        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }
}
