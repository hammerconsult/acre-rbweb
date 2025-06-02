/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.ListenerAlteraSituacaoParcela;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gustavo
 */
@GrupoDiagrama(nome = "Tributario")
@Entity

@Audited
@EntityListeners(ListenerAlteraSituacaoParcela.class)
public class SituacaoParcelaValorDivida extends SuperEntidade implements Serializable, Comparable<SituacaoParcelaValorDivida> {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataLancamento;
    @Enumerated(EnumType.STRING)
    private SituacaoParcela situacaoParcela;
    @ManyToOne
    private ParcelaValorDivida parcela;
    private BigDecimal saldo;
    private Boolean inconsistente;
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "situacao")
    private List<SituacaoParcelaIntegracao> integracoes;
    private String referencia;
    private Boolean processada;
    @Transient
    private boolean geraReferencia = true;

    public SituacaoParcelaValorDivida() {
        dataLancamento = new Date();
        inconsistente = false;
        integracoes = new ArrayList<>();
    }

    public SituacaoParcelaValorDivida(SituacaoParcela situacaoParcela, ParcelaValorDivida parcela, BigDecimal saldo) {
        this.saldo = saldo != null ? saldo : BigDecimal.ZERO;
        this.dataLancamento = new Date();
        this.inconsistente = (this.saldo.compareTo(BigDecimal.ZERO) != 0) && (situacaoParcela.equals(SituacaoParcela.PAGO));
        this.situacaoParcela = situacaoParcela;
        this.parcela = parcela;
    }

    public SituacaoParcelaValorDivida(SituacaoParcela situacaoParcela, ParcelaValorDivida parcela, BigDecimal saldo, Boolean geraReferencia, String referencia) {
        this.saldo = saldo != null ? saldo : BigDecimal.ZERO;
        this.dataLancamento = new Date();
        this.inconsistente = (this.saldo.compareTo(BigDecimal.ZERO) != 0) && (situacaoParcela.equals(SituacaoParcela.PAGO));
        this.situacaoParcela = situacaoParcela;
        this.parcela = parcela;
        this.geraReferencia = geraReferencia;
        this.referencia = referencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isGeraReferencia() {
        return geraReferencia;
    }

    public void setGeraReferencia(boolean geraReferencia) {
        this.geraReferencia = geraReferencia;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Boolean isInconsistente() {
        return inconsistente;
    }

    public void setInconsistente(Boolean inconsistente) {
        this.inconsistente = inconsistente;
    }

    public List<SituacaoParcelaIntegracao> getIntegracoes() {
        return integracoes;
    }

    public void setIntegracoes(List<SituacaoParcelaIntegracao> integracoes) {
        this.integracoes = integracoes;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    @Override
    public int compareTo(SituacaoParcelaValorDivida o) {
        if (this.dataLancamento == null && o.getDataLancamento() != null) {
            return -1;
        } else if (this.dataLancamento == null && o.getDataLancamento() == null) {
            return 0;
        } else if (this.dataLancamento != null && o.getDataLancamento() == null) {
            return 1;
        } else {
            return this.dataLancamento.compareTo(o.getDataLancamento());
        }
    }

    public void geraReferencia() {
        if (geraReferencia) {
            try {
                parcela.getValorDivida().getCalculo().getTipoCalculo().geraReferencia(this);
            } catch (Exception e) {
                LoggerFactory.getLogger(SituacaoParcelaValorDivida.class).info("Erro ao gerar referencia em situação parcela valor dívida: " + this.getParcela() + " - " + this.getSituacaoParcela().getDescricao());
            }
        }
    }

    public boolean isSituacaoPago() {
        return SituacaoParcela.PAGO.equals(situacaoParcela);
    }

    public boolean isProcessada() {
        return processada != null ? processada : false;
    }

    public void setProcessada(boolean processada) {
        this.processada = processada;
    }
}
