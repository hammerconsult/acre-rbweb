package br.com.webpublico.entidades;


import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.administrativo.AgrupadorMovimentoBem;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MovimentoBloqueioBem extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Bem")
    private Bem bem;

    @Obrigatorio
    @Etiqueta("Data do Bloqueio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataMovimento;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação")
    private OperacaoMovimentacaoBem operacaoMovimento;

    /**
     * Campo para acompanhar a situação do evento do bem
     */
    @Etiqueta("Situação do Evento")
    @Enumerated(EnumType.STRING)
    private SituacaoEventoBem situacaoEventoBem;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Agrupador    ")
    private AgrupadorMovimentoBem agrupador;

    /**
     * @return boolean false para liberado e true para bloqueado
     */
    private Boolean movimentoUm;
    private Boolean movimentoDois;
    private Boolean movimentoTres;

    public MovimentoBloqueioBem() {
        movimentoUm = Boolean.FALSE;
        movimentoDois = Boolean.FALSE;
        movimentoTres = Boolean.FALSE;
    }

    public MovimentoBloqueioBem(Date dataMovimento, OperacaoMovimentacaoBem operacaoMovimento, Bem bem, SituacaoEventoBem situacaoEventoBem, AgrupadorMovimentoBem agrupador) {
        this.dataMovimento = dataMovimento;
        this.operacaoMovimento = operacaoMovimento;
        this.bem = bem;
        this.situacaoEventoBem = situacaoEventoBem;
        this.agrupador = agrupador;
        this.movimentoUm = Boolean.FALSE;
        this.movimentoDois = Boolean.FALSE;
        this.movimentoTres = Boolean.FALSE;
    }

    public AgrupadorMovimentoBem getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(AgrupadorMovimentoBem agrupador) {
        this.agrupador = agrupador;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public OperacaoMovimentacaoBem getOperacaoMovimento() {
        return operacaoMovimento;
    }

    public void setOperacaoMovimento(OperacaoMovimentacaoBem operacaoMovimento) {
        this.operacaoMovimento = operacaoMovimento;
    }

    public Boolean getMovimentoUm() {
        return movimentoUm;
    }

    public void setMovimentoUm(Boolean movimentoUm) {
        this.movimentoUm = movimentoUm;
    }

    public Boolean getMovimentoDois() {
        return movimentoDois;
    }

    public void setMovimentoDois(Boolean movimentoDois) {
        this.movimentoDois = movimentoDois;
    }

    public Boolean getMovimentoTres() {
        return movimentoTres;
    }

    public void setMovimentoTres(Boolean movimentoTres) {
        this.movimentoTres = movimentoTres;
    }

    public boolean isBloqueado(){
        return movimentoUm || movimentoDois || movimentoTres;
    }

    public SituacaoEventoBem getSituacaoEventoBem() {
        return situacaoEventoBem;
    }

    public void setSituacaoEventoBem(SituacaoEventoBem situacaoEventoBem) {
        this.situacaoEventoBem = situacaoEventoBem;
    }
}
