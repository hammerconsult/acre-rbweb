package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 07/06/2016.
 */
public class PagamentoPorCredor {

    private String pessoa;
    private String cpfCnpj;
    private Date dataPagamento;
    private String numeroPagamento;
    private Date dataEmissaoPagamento;
    private String contaFinanceira;
    private String ordemBancaria;
    private String numeroDocumento;
    private Date dataEmpenho;
    private String numeroEmpenho;
    private String programaTrabalho;
    private String contaDespesa;
    private String fonteDeRecurso;
    private BigDecimal valorPagamento;
    private List<PagamentoPorCredorRetencao> retencoes;

    public PagamentoPorCredor() {
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getNumeroPagamento() {
        return numeroPagamento;
    }

    public void setNumeroPagamento(String numeroPagamento) {
        this.numeroPagamento = numeroPagamento;
    }

    public Date getDataEmissaoPagamento() {
        return dataEmissaoPagamento;
    }

    public void setDataEmissaoPagamento(Date dataEmissaoPagamento) {
        this.dataEmissaoPagamento = dataEmissaoPagamento;
    }

    public String getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(String contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public String getOrdemBancaria() {
        return ordemBancaria;
    }

    public void setOrdemBancaria(String ordemBancaria) {
        this.ordemBancaria = ordemBancaria;
    }

    public Date getDataEmpenho() {
        return dataEmpenho;
    }

    public void setDataEmpenho(Date dataEmpenho) {
        this.dataEmpenho = dataEmpenho;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public String getProgramaTrabalho() {
        return programaTrabalho;
    }

    public void setProgramaTrabalho(String programaTrabalho) {
        this.programaTrabalho = programaTrabalho;
    }

    public String getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(String contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public String getFonteDeRecurso() {
        return fonteDeRecurso;
    }

    public void setFonteDeRecurso(String fonteDeRecurso) {
        this.fonteDeRecurso = fonteDeRecurso;
    }

    public BigDecimal getValorPagamento() {
        return valorPagamento;
    }

    public void setValorPagamento(BigDecimal valorPagamento) {
        this.valorPagamento = valorPagamento;
    }

    public List<PagamentoPorCredorRetencao> getRetencoes() {
        return retencoes;
    }

    public void setRetencoes(List<PagamentoPorCredorRetencao> retencoes) {
        this.retencoes = retencoes;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
}
