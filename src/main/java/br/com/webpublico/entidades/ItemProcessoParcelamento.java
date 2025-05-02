/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Tributario")
public class ItemProcessoParcelamento implements Serializable, Comparable<ItemProcessoParcelamento> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoParcelamento processoParcelamento;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcao;
    private BigDecimal honorarios;
    @Enumerated(EnumType.STRING)
    private SituacaoParcela situacaoAnterior;
    @Transient
    private Long criadoEm = System.nanoTime();
    @Transient
    private String numeroProcessoAjuizamento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public ProcessoParcelamento getProcessoParcelamento() {
        return processoParcelamento;
    }

    public void setProcessoParcelamento(ProcessoParcelamento processoParcelamento) {
        this.processoParcelamento = processoParcelamento;
    }

    public SituacaoParcela getSituacaoAnterior() {
        return situacaoAnterior;
    }

    public void setSituacaoAnterior(SituacaoParcela situacaoAnterior) {
        this.situacaoAnterior = situacaoAnterior;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public BigDecimal getImposto() {
        return imposto != null ? imposto :
            BigDecimal.ZERO;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa != null ? taxa :
            BigDecimal.ZERO;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getJuros() {
        return juros != null ? juros :
            BigDecimal.ZERO;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa != null ? multa :
            BigDecimal.ZERO;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecao() {
        return correcao != null ? correcao :
            BigDecimal.ZERO;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public BigDecimal getHonorarios() {
        return honorarios != null ? honorarios :
            BigDecimal.ZERO;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public BigDecimal getTotal() {
        return getImposto().add(getTaxa().add(getJuros().add(getMulta().add(getCorrecao().add(getHonorarios())))));
    }

    public String getNumeroProcessoAjuizamento() {
        return numeroProcessoAjuizamento;
    }

    public void setNumeroProcessoAjuizamento(String numeroProcessoAjuizamento) {
        this.numeroProcessoAjuizamento = numeroProcessoAjuizamento;
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
        return "br.com.webpublico.entidades.ItemProcessoParcelamento[ id=" + id + " ]";
    }

    @Override
    public int compareTo(ItemProcessoParcelamento o) {
        return parcelaValorDivida.getVencimento().compareTo(o.getParcelaValorDivida().getVencimento());
    }
}
