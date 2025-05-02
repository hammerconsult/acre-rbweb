package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 10/11/14
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
public class SaldoContaFinanceiraXSaldoBancario {

    private Long idContaBacaria;
    private String banco;
    private String agencia;
    private String numeroConta;
    private String descricaoConta;
    private String contaBancaria;
    private BigDecimal saldoBancario;
    private BigDecimal saldoContaFinanceira;
    private List<SaldoContaFinanceiraXSaldoBancarioItem> contasFinanceiras;

    public SaldoContaFinanceiraXSaldoBancario() {
    }

    public String getContaBancaria() {
        return contaBancaria;
    }

    public void setContaBancaria(String contaBancaria) {
        this.contaBancaria = contaBancaria;
    }

    public BigDecimal getSaldoContaFinanceira() {
        return saldoContaFinanceira;
    }

    public void setSaldoContaFinanceira(BigDecimal saldoContaFinanceira) {
        this.saldoContaFinanceira = saldoContaFinanceira;
    }

    public String getDescricaoConta() {
        return descricaoConta;
    }

    public void setDescricaoConta(String descricaoConta) {
        this.descricaoConta = descricaoConta;
    }

    public BigDecimal getSaldoBancario() {
        return saldoBancario;
    }

    public void setSaldoBancario(BigDecimal saldoBancario) {
        this.saldoBancario = saldoBancario;
    }

    public Long getIdContaBacaria() {
        return idContaBacaria;
    }

    public void setIdContaBacaria(Long idContaBacaria) {
        this.idContaBacaria = idContaBacaria;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public List<SaldoContaFinanceiraXSaldoBancarioItem> getContasFinanceiras() {
        return contasFinanceiras;
    }

    public void setContasFinanceiras(List<SaldoContaFinanceiraXSaldoBancarioItem> contasFinanceiras) {
        this.contasFinanceiras = contasFinanceiras;
    }
}
