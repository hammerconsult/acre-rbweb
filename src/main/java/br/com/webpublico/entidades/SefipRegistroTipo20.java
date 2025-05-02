/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.StringUtil;

import java.io.Serializable;

/**
 * @author andre
 */
public class SefipRegistroTipo20 implements Serializable {

    private String ordenacao;
    private final String tipoDeRegistro = "20";
    private String tipoDeInscricao;
    private String inscricaoDaEmpresa;
    private String tipoInscricaoTomador;
    private String inscricaoTomador;
    private String zeros;
    private String nomeTomador;
    private String logradouro;
    private String bairro;
    private String cep;
    private String cidade;
    private String unidadeDaFederacao;
    private String codigoPagamentoGPS;
    private String salarioFamilia;
    private String contribuicaoDescontadaEmpregado13;
    private String indicadorValorNegativoPositivo;
    private String valorDevidoPrevidenciaSocial;
    private String valorRetencao;
    private String valorFaturasEmitidas;
    private String zeros2;
    private String brancos;
    private final String finalDeLinha = "*";

    public SefipRegistroTipo20() {
    }

    public SefipRegistroTipo20(String ordenacao, String tipoDeInscricao, String inscricaoDaEmpresa, String tipoInscricaoTomador, String inscricaoTomador, String zeros, String nomeTomador, String logradouro, String bairro, String cep, String cidade, String unidadeDaFederacao, String codigoPagamentoGPS, String salarioFamilia, String contribuicaoDescontadaEmpregado13, String indicadorValorNegativoPositivo, String valorDevidoPrevidenciaSocial, String valorRetencao, String valorFaturasEmitidas, String zeros2, String brancos) {
        this.ordenacao = ordenacao;
        this.tipoDeInscricao = tipoDeInscricao;
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
        this.tipoInscricaoTomador = tipoInscricaoTomador;
        this.inscricaoTomador = inscricaoTomador;
        this.zeros = zeros;
        this.nomeTomador = nomeTomador;
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cep = cep;
        this.cidade = cidade;
        this.unidadeDaFederacao = unidadeDaFederacao;
        this.codigoPagamentoGPS = codigoPagamentoGPS;
        this.salarioFamilia = salarioFamilia;
        this.contribuicaoDescontadaEmpregado13 = contribuicaoDescontadaEmpregado13;
        this.indicadorValorNegativoPositivo = indicadorValorNegativoPositivo;
        this.valorDevidoPrevidenciaSocial = valorDevidoPrevidenciaSocial;
        this.valorRetencao = valorRetencao;
        this.valorFaturasEmitidas = valorFaturasEmitidas;
        this.zeros2 = zeros2;
        this.brancos = brancos;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getBrancos() {
        return brancos;
    }

    public void setBrancos(String brancos) {
        this.brancos = brancos;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCodigoPagamentoGPS() {
        return codigoPagamentoGPS;
    }

    public void setCodigoPagamentoGPS(String codigoPagamentoGPS) {
        this.codigoPagamentoGPS = codigoPagamentoGPS;
    }

    public String getContribuicaoDescontadaEmpregado13() {
        return contribuicaoDescontadaEmpregado13;
    }

    public void setContribuicaoDescontadaEmpregado13(String contribuicaoDescontadaEmpregado13) {
        this.contribuicaoDescontadaEmpregado13 = contribuicaoDescontadaEmpregado13;
    }

    public String getIndicadorValorNegativoPositivo() {
        return indicadorValorNegativoPositivo;
    }

    public void setIndicadorValorNegativoPositivo(String indicadorValorNegativoPositivo) {
        this.indicadorValorNegativoPositivo = indicadorValorNegativoPositivo;
    }

    public String getInscricaoDaEmpresa() {
        return inscricaoDaEmpresa;
    }

    public void setInscricaoDaEmpresa(String inscricaoDaEmpresa) {
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
    }

    public String getInscricaoTomador() {
        return inscricaoTomador;
    }

    public void setInscricaoTomador(String inscricaoTomador) {
        this.inscricaoTomador = inscricaoTomador;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNomeTomador() {
        return nomeTomador;
    }

    public void setNomeTomador(String nomeTomador) {
        this.nomeTomador = nomeTomador;
    }

    public String getSalarioFamilia() {
        return salarioFamilia;
    }

    public void setSalarioFamilia(String salarioFamilia) {
        this.salarioFamilia = salarioFamilia;
    }

    public String getTipoDeInscricao() {
        return tipoDeInscricao;
    }

    public void setTipoDeInscricao(String tipoDeInscricao) {
        this.tipoDeInscricao = tipoDeInscricao;
    }

    public String getTipoInscricaoTomador() {
        return tipoInscricaoTomador;
    }

    public void setTipoInscricaoTomador(String tipoInscricaoTomador) {
        this.tipoInscricaoTomador = tipoInscricaoTomador;
    }

    public String getUnidadeDaFederacao() {
        return unidadeDaFederacao;
    }

    public void setUnidadeDaFederacao(String unidadeDaFederacao) {
        this.unidadeDaFederacao = unidadeDaFederacao;
    }

    public String getValorDevidoPrevidenciaSocial() {
        return valorDevidoPrevidenciaSocial;
    }

    public void setValorDevidoPrevidenciaSocial(String valorDevidoPrevidenciaSocial) {
        this.valorDevidoPrevidenciaSocial = valorDevidoPrevidenciaSocial;
    }

    public String getValorFaturasEmitidas() {
        return valorFaturasEmitidas;
    }

    public void setValorFaturasEmitidas(String valorFaturasEmitidas) {
        this.valorFaturasEmitidas = valorFaturasEmitidas;
    }

    public String getValorRetencao() {
        return valorRetencao;
    }

    public void setValorRetencao(String valorRetencao) {
        this.valorRetencao = valorRetencao;
    }

    public String getZeros() {
        return zeros;
    }

    public void setZeros(String zeros) {
        this.zeros = zeros;
    }

    public String getZeros2() {
        return zeros2;
    }

    public void setZeros2(String zeros2) {
        this.zeros2 = zeros2;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public String getRegistroTipo20() {
        StringBuilder texto = new StringBuilder("");

        //Ordenacão, é removido posteriormente
        texto.append(ordenacao);
        //Tipo de Registro - 2 Posições
        texto.append(tipoDeRegistro);
        //Tipo de Inscrição - 1 Posição
        texto.append(StringUtil.cortaOuCompletaDireita(tipoDeInscricao, 1, " "));
        //Inscricao da Empresa - 14 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(inscricaoDaEmpresa.replace(".", "").replace("/", ""), 14, " "));
        //Tipo de Inscricao - 1 Posição
        texto.append(StringUtil.cortaOuCompletaDireita(tipoInscricaoTomador, 1, " "));
        //Inscricao do Tomador - 14 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(inscricaoTomador, 14, " "));
        //Zeros - 21 posições
        texto.append(StringUtil.cortaOuCompletaDireita(zeros, 21, "0"));
        //Nome do Tomador -40 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(nomeTomador, 40, " "));
        //Logradouro - 50 posições
        texto.append(StringUtil.cortaOuCompletaDireita(logradouro, 50, " "));
        //Bairro - 20 posições
        texto.append(StringUtil.cortaOuCompletaDireita(bairro, 20, " "));
        //CEP - 8 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(cep, 8, " "));
        //Cidade - 20 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(cidade, 20, " "));
        //Unidade da Federação - 2 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(unidadeDaFederacao, 2, " "));
        //Código de Pagamento GPS - 4
        texto.append(StringUtil.cortaOuCompletaDireita(codigoPagamentoGPS, 4, " "));
        //Salário Família - 15 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(salarioFamilia, 15, "0"));
        //Contrib. Desc Empregado Referente a competência 13 - 15 Posicaoes
        texto.append(StringUtil.cortaOuCompletaEsquerda(contribuicaoDescontadaEmpregado13, 15, "0"));
        //Indicador de Valor Negativo ou Positivo - 1 Posicao
        texto.append(StringUtil.cortaOuCompletaDireita(indicadorValorNegativoPositivo, 1, "0"));
        //Valor Devido a Previdencia Social - 14 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(valorDevidoPrevidenciaSocial, 14, "0"));
        //Valor de Retenção - 15 posições
        texto.append(StringUtil.cortaOuCompletaDireita(valorRetencao, 15, "0"));
        //Valor das Faturas Emitidas - 15 posições
        texto.append(StringUtil.cortaOuCompletaDireita(valorFaturasEmitidas, 15, "0"));
        //Zeros - 45 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(zeros2, 45, "0"));
        //Brancos - 42 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(brancos, 42, " "));
        String s = StringUtil.removeCaracteresEspeciais(texto.toString());
        //Final de Linha - 1 Posição
        s += StringUtil.cortaOuCompletaEsquerda(finalDeLinha, 1, "*");
        s += "\r\n";
        return s;
    }
}
