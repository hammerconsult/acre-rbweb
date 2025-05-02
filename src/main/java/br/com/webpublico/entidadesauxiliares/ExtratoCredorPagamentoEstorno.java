package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Mateus on 01/12/2014.
 */
public class ExtratoCredorPagamentoEstorno {
    private String dataRegistro;
    private String numero;
    private String operacao;
    private BigDecimal valor;
    private String numeroPagamento;
    private Long estornoId;
    private List<ExtratoCredorRetencao> retencoes;

    public ExtratoCredorPagamentoEstorno() {
    }

    public String getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(String dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
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

    public String getNumeroPagamento() {
        return numeroPagamento;
    }

    public void setNumeroPagamento(String numeroPagamento) {
        this.numeroPagamento = numeroPagamento;
    }

    public Long getEstornoId() {
        return estornoId;
    }

    public void setEstornoId(Long estornoId) {
        this.estornoId = estornoId;
    }

    public List<ExtratoCredorRetencao> getRetencoes() {
        return retencoes;
    }

    public void setRetencoes(List<ExtratoCredorRetencao> retencoes) {
        this.retencoes = retencoes;
    }
}
