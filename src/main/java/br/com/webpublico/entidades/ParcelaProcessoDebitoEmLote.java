package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class ParcelaProcessoDebitoEmLote extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParcelaValorDivida parcela;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    private ProcessoDebitoEmLote processoDebitoEmLote;
    @Enumerated(EnumType.STRING)
    private SituacaoParcela situacaoAnterior;
    @Enumerated(EnumType.STRING)
    private SituacaoParcela situacaoProxima;
    private String referencia;

    @Transient
    private ResultadoParcela resultadoParcela;

    public ParcelaProcessoDebitoEmLote() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }

    public Long getCriadoEm() {
        return this.criadoEm;
    }

    public ProcessoDebitoEmLote getProcessoDebitoEmLote() {
        return processoDebitoEmLote;
    }

    public void setProcessoDebitoEmLote(ProcessoDebitoEmLote processoDebitoEmLote) {
        this.processoDebitoEmLote = processoDebitoEmLote;
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
}
