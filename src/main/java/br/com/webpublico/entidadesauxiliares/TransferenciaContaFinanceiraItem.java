package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 20/06/14
 * Time: 08:50
 * To change this template use File | Settings | File Templates.
 */
public class TransferenciaContaFinanceiraItem {
    private String numero;
    private Date dataTranferencia;
    private BigDecimal valor;
    private BigDecimal saldo;
    private String resultanteIndependente;
    private String tipoLiberacaoFinanceira;
    private String contaRetirada;
    private String contaDeposito;
    private String fonteRetirada;
    private String fonteDeposito;
    private String unidade;
    private String orgao;
    private String unidadeGestora;

    public TransferenciaContaFinanceiraItem() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataTranferencia() {
        return dataTranferencia;
    }

    public void setDataTranferencia(Date dataTranferencia) {
        this.dataTranferencia = dataTranferencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getResultanteIndependente() {
        return resultanteIndependente;
    }

    public void setResultanteIndependente(String resultanteIndependente) {
        this.resultanteIndependente = resultanteIndependente;
    }

    public String getTipoLiberacaoFinanceira() {
        return tipoLiberacaoFinanceira;
    }

    public void setTipoLiberacaoFinanceira(String tipoLiberacaoFinanceira) {
        this.tipoLiberacaoFinanceira = tipoLiberacaoFinanceira;
    }

    public String getContaRetirada() {
        return contaRetirada;
    }

    public void setContaRetirada(String contaRetirada) {
        this.contaRetirada = contaRetirada;
    }

    public String getContaDeposito() {
        return contaDeposito;
    }

    public void setContaDeposito(String contaDeposito) {
        this.contaDeposito = contaDeposito;
    }

    public String getFonteRetirada() {
        return fonteRetirada;
    }

    public void setFonteRetirada(String fonteRetirada) {
        this.fonteRetirada = fonteRetirada;
    }

    public String getFonteDeposito() {
        return fonteDeposito;
    }

    public void setFonteDeposito(String fonteDeposito) {
        this.fonteDeposito = fonteDeposito;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }
}
