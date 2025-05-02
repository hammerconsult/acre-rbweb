package br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil;

import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.TipoOperacaoORC;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SaldoFonteDespesaORCDTO {
    private Long id;
    private Long idFonte;
    private OperacaoORC operacaoOrc;
    private TipoOperacaoORC tipoCredito;
    private BigDecimal valor;
    private LocalDate data;
    private Long idUnidade;
    private String idOrigem;
    private String classeOrigem;
    private String numeroMovimento;
    private String historico;
    private Boolean realizarValidacoes;

    public SaldoFonteDespesaORCDTO() {
    }

    public Long getIdFonte() {
        return idFonte;
    }

    public void setIdFonte(Long idFonte) {
        this.idFonte = idFonte;
    }

    public OperacaoORC getOperacaoOrc() {
        return operacaoOrc;
    }

    public void setOperacaoOrc(OperacaoORC operacaoOrc) {
        this.operacaoOrc = operacaoOrc;
    }

    public TipoOperacaoORC getTipoCredito() {
        return tipoCredito;
    }

    public void setTipoCredito(TipoOperacaoORC tipoCredito) {
        this.tipoCredito = tipoCredito;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Long getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Long idUnidade) {
        this.idUnidade = idUnidade;
    }

    public String getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(String idOrigem) {
        this.idOrigem = idOrigem;
    }

    public String getClasseOrigem() {
        return classeOrigem;
    }

    public void setClasseOrigem(String classeOrigem) {
        this.classeOrigem = classeOrigem;
    }

    public String getNumeroMovimento() {
        return numeroMovimento;
    }

    public void setNumeroMovimento(String numeroMovimento) {
        this.numeroMovimento = numeroMovimento;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getRealizarValidacoes() {
        return realizarValidacoes;
    }

    public void setRealizarValidacoes(Boolean realizarValidacoes) {
        this.realizarValidacoes = realizarValidacoes;
    }
}
