package br.com.webpublico.entidades;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity

@GrupoDiagrama(nome = "Tributario")
@Audited
public class ItemProcessoDebito implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    private Long criadoEm = System.nanoTime();
    @ManyToOne
    private ProcessoDebito processoDebito;
    @ManyToOne
    private ParcelaValorDivida parcela;
    @Enumerated(EnumType.STRING)
    private SituacaoParcela situacaoAnterior;
    @Enumerated(EnumType.STRING)
    private SituacaoParcela situacaoProxima;
    private String referencia;
    @Transient
    private ResultadoParcela resultadoParcela;
    @OneToMany(mappedBy = "itemProcessoDebito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemProcessoDebitoParcela> itemProcessoDebitoParcela;

    @OneToMany(mappedBy = "itemProcessoDebito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemProcessoDebitoParcelamento> itemProcessoDebitoParcelamento;

    public ItemProcessoDebito() {
        itemProcessoDebitoParcela = Lists.newArrayList();
        itemProcessoDebitoParcelamento = Lists.newArrayList();
    }

    public List<ItemProcessoDebitoParcela> getItemProcessoDebitoParcela() {
        return itemProcessoDebitoParcela;
    }

    public void setItemProcessoDebitoParcela(List<ItemProcessoDebitoParcela> itemProcessoDebitoParcela) {
        this.itemProcessoDebitoParcela = itemProcessoDebitoParcela;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return this.criadoEm;
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public ProcessoDebito getProcessoDebito() {
        return processoDebito;
    }

    public void setProcessoDebito(ProcessoDebito processoDebito) {
        this.processoDebito = processoDebito;
    }

    public SituacaoParcela getSituacaoAnterior() {
        return situacaoAnterior;
    }

    public void setSituacaoAnterior(SituacaoParcela situacaoAnterior) {
        this.situacaoAnterior = situacaoAnterior;
    }

    public SituacaoParcela getSituacaoProxima() {
        return situacaoProxima;
    }

    public void setSituacaoProxima(SituacaoParcela situacaoProxima) {
        this.situacaoProxima = situacaoProxima;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public List<ItemProcessoDebitoParcelamento> getItemProcessoDebitoParcelamento() {
        return itemProcessoDebitoParcelamento;
    }

    public void setItemProcessoDebitoParcelamento(List<ItemProcessoDebitoParcelamento> itemProcessoDebitoParcelamento) {
        this.itemProcessoDebitoParcelamento = itemProcessoDebitoParcelamento;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
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
        return "br.com.webpublico.entidades.ItemProcessoDebito[ id=" + id + " ]";
    }
}
