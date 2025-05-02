package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;

public class FichaFinanceiraFPItensVO {
    private BigDecimal valor;
    private BigDecimal valorReferencia;
    private BigDecimal valorBaseCalculo;
    private Integer mes;
    private Integer ano;
    private String eventoCodigo;
    private String eventoDesc;
    private String tipoBaseCodigo;
    private String eventoTipo;
    private String tipoLancamento;
    private String complementoReferencia;
    private String ordenacao;
    private String deduzIRRF;
    private String deduzRPPS;
    private String deduzINSS;

    public FichaFinanceiraFPItensVO() {
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValorReferencia() {
        return valorReferencia;
    }

    public void setValorReferencia(BigDecimal valorReferencia) {
        this.valorReferencia = valorReferencia;
    }

    public BigDecimal getValorBaseCalculo() {
        return valorBaseCalculo;
    }

    public void setValorBaseCalculo(BigDecimal valorBaseCalculo) {
        this.valorBaseCalculo = valorBaseCalculo;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getEventoCodigo() {
        return eventoCodigo;
    }

    public void setEventoCodigo(String eventoCodigo) {
        this.eventoCodigo = eventoCodigo;
    }

    public String getEventoDesc() {
        return eventoDesc;
    }

    public void setEventoDesc(String eventoDesc) {
        this.eventoDesc = eventoDesc;
    }

    public String getTipoBaseCodigo() {
        return tipoBaseCodigo;
    }

    public void setTipoBaseCodigo(String tipoBaseCodigo) {
        this.tipoBaseCodigo = tipoBaseCodigo;
    }

    public String getEventoTipo() {
        return eventoTipo;
    }

    public void setEventoTipo(String eventoTipo) {
        this.eventoTipo = eventoTipo;
    }

    public String getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(String tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public String getComplementoReferencia() {
        return complementoReferencia;
    }

    public void setComplementoReferencia(String complementoReferencia) {
        this.complementoReferencia = complementoReferencia;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public String getDeduzIRRF() {
        return deduzIRRF;
    }

    public void setDeduzIRRF(String deduzIRRF) {
        this.deduzIRRF = deduzIRRF;
    }

    public String getDeduzRPPS() {
        return deduzRPPS;
    }

    public void setDeduzRPPS(String deduzRPPS) {
        this.deduzRPPS = deduzRPPS;
    }

    public String getDeduzINSS() {
        return deduzINSS;
    }

    public void setDeduzINSS(String deduzINSS) {
        this.deduzINSS = deduzINSS;
    }
}
