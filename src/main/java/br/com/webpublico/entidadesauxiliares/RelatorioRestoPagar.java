/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Edi
 */
public class RelatorioRestoPagar {

    private Date data;
    private String numero;
    private String referencia;
    private String conta;
    private String pessoa;
    private String descricaoOrgao;
    private String codigoOrgao;
    private String descricaoUnidade;
    private String codigoUnidade;
    private String descricaoFonte;
    private String codigoFonte;
    private BigDecimal processados;
    private BigDecimal naoProcessados;
    private BigDecimal contadorRegistro;

    public RelatorioRestoPagar() {
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public BigDecimal getProcessados() {
        return processados;
    }

    public void setProcessados(BigDecimal processados) {
        this.processados = processados;
    }

    public BigDecimal getNaoProcessados() {
        return naoProcessados;
    }

    public void setNaoProcessados(BigDecimal naoProcessados) {
        this.naoProcessados = naoProcessados;
    }

    public String getDescricaoOrgao() {
        return descricaoOrgao;
    }

    public void setDescricaoOrgao(String descricaoOrgao) {
        this.descricaoOrgao = descricaoOrgao;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getDescricaoFonte() {
        return descricaoFonte;
    }

    public void setDescricaoFonte(String descricaoFonte) {
        this.descricaoFonte = descricaoFonte;
    }

    public String getCodigoFonte() {
        return codigoFonte;
    }

    public void setCodigoFonte(String codigoFonte) {
        this.codigoFonte = codigoFonte;
    }

    public BigDecimal getContadorRegistro() {
        return contadorRegistro;
    }

    public void setContadorRegistro(BigDecimal contadorRegistro) {
        this.contadorRegistro = contadorRegistro;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
