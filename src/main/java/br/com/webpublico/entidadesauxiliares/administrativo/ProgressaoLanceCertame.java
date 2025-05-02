package br.com.webpublico.entidadesauxiliares.administrativo;

import br.com.webpublico.entidades.Pessoa;

import java.math.BigDecimal;

public class ProgressaoLanceCertame {
    private String rodada;
    private String numeroLote;
    private String descricao;
    private String item;
    private String marca;
    private Pessoa participante;
    private String responsavel;
    private BigDecimal valor;
    private BigDecimal valorPrevisto;
    private BigDecimal lanceAnterior;
    private BigDecimal lanceAtual;
    private String situacao;

    public String getRodada() {
        return rodada;
    }

    public void setRodada(String rodada) {
        this.rodada = rodada;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Pessoa getParticipante() {
        return participante;
    }

    public void setParticipante(Pessoa participante) {
        this.participante = participante;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValorPrevisto() {
        return valorPrevisto;
    }

    public void setValorPrevisto(BigDecimal valorPrevisto) {
        this.valorPrevisto = valorPrevisto;
    }

    public BigDecimal getLanceAnterior() {
        return lanceAnterior;
    }

    public void setLanceAnterior(BigDecimal lanceAnterior) {
        this.lanceAnterior = lanceAnterior;
    }

    public BigDecimal getLanceAtual() {
        return lanceAtual;
    }

    public void setLanceAtual(BigDecimal lanceAtual) {
        this.lanceAtual = lanceAtual;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
