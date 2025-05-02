package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 01/12/2014.
 */
public class ExtratoCredorPagamento {
    private Long pagamentoId;
    private String previsto;
    private String dataRegistro;
    private String numero;
    private String operacao;
    private BigDecimal valor;
    private String status;
    private String numeroLiquidacao;
    private String subConta;
    private BigDecimal valorRetencoes;
    private List<ExtratoCredorPagamentoEstorno> estornos;
    private List<ExtratoCredorRetencao> retencoes;
    private List<ExtratoCredorBordero> borderos;

    public ExtratoCredorPagamento() {
        estornos = new ArrayList<>();
    }

    public Long getPagamentoId() {
        return pagamentoId;
    }

    public void setPagamentoId(Long pagamentoId) {
        this.pagamentoId = pagamentoId;
    }

    public String getPrevisto() {
        return previsto;
    }

    public void setPrevisto(String previsto) {
        this.previsto = previsto;
    }

    public String getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(String dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNumeroLiquidacao() {
        return numeroLiquidacao;
    }

    public void setNumeroLiquidacao(String numeroLiquidacao) {
        this.numeroLiquidacao = numeroLiquidacao;
    }

    public String getSubConta() {
        return subConta;
    }

    public void setSubConta(String subConta) {
        this.subConta = subConta;
    }

    public List<ExtratoCredorPagamentoEstorno> getEstornos() {
        return estornos;
    }

    public void setEstornos(List<ExtratoCredorPagamentoEstorno> estornos) {
        this.estornos = estornos;
    }

    public BigDecimal getValorRetencoes() {
        return valorRetencoes;
    }

    public void setValorRetencoes(BigDecimal valorRetencoes) {
        this.valorRetencoes = valorRetencoes;
    }

    public List<ExtratoCredorRetencao> getRetencoes() {
        return retencoes;
    }

    public void setRetencoes(List<ExtratoCredorRetencao> retencoes) {
        this.retencoes = retencoes;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public List<ExtratoCredorBordero> getBorderos() {
        return borderos;
    }

    public void setBorderos(List<ExtratoCredorBordero> borderos) {
        this.borderos = borderos;
    }
}
