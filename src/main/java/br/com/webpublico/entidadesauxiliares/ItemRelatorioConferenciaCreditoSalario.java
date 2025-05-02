package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Buzatto on 07/03/2016.
 */
public class ItemRelatorioConferenciaCreditoSalario implements Comparable<ItemRelatorioConferenciaCreditoSalario> {

    private String numeroBanco;
    private String numeroAgencia;
    private String digitoAgencia;
    private String numeroConta;
    private String digitoConta;
    private String nomeServidor;
    private String matriculaServidor;
    private String numeroContrato;
    private BigDecimal valor;
    private String cpfServidor;

    public ItemRelatorioConferenciaCreditoSalario() {
    }

    public String getNumeroBanco() {
        return numeroBanco;
    }

    public void setNumeroBanco(String numeroBanco) {
        this.numeroBanco = numeroBanco;
    }

    public String getNumeroAgencia() {
        return numeroAgencia;
    }

    public void setNumeroAgencia(String numeroAgencia) {
        this.numeroAgencia = numeroAgencia;
    }

    public String getDigitoAgencia() {
        return digitoAgencia;
    }

    public void setDigitoAgencia(String digitoAgencia) {
        this.digitoAgencia = digitoAgencia;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getDigitoConta() {
        return digitoConta;
    }

    public void setDigitoConta(String digitoConta) {
        this.digitoConta = digitoConta;
    }

    public String getNomeServidor() {
        return nomeServidor;
    }

    public void setNomeServidor(String nomeServidor) {
        this.nomeServidor = nomeServidor;
    }

    public String getMatriculaServidor() {
        return matriculaServidor;
    }

    public void setMatriculaServidor(String matriculaServidor) {
        this.matriculaServidor = matriculaServidor;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getCpfServidor() {
        return cpfServidor;
    }

    public void setCpfServidor(String cpfServidor) {
        this.cpfServidor = cpfServidor;
    }

    @Override
    public int compareTo(ItemRelatorioConferenciaCreditoSalario item) {
        return this.nomeServidor.substring(16, nomeServidor.length()).compareTo(item.getNomeServidor().substring(16, item.getNomeServidor().length()));
    }
}
