/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @author andre
 */
public class SefipRegistroTipo50 implements Serializable {

    private String ordenacao;
    private final String tipoDeRegistro = "50";
    private String tipoDeInscricaoEmpresa;
    private String inscricaoDaEmpresa;
    private String zeros;
    private String nomeEmpresa;
    private String tipoDeInscricaoTomador;
    private String inscricaoDoTomador;
    private String nomeDoTomador;
    private String logradouro;
    private String bairro;
    private String cep;
    private String cidade;
    private String unidadeDaFederacao;
    private String telefone;
    private String cnae;
    private String codigoDeCentralizacao;
    private String valorDaMulta;
    private String brancos;
    private final String finalDeLinha = "*";

    public SefipRegistroTipo50() {
    }

    public SefipRegistroTipo50(String ordenacao, String tipoDeInscricaoEmpresa, String inscricaoDaEmpresa, String zeros, String nomeEmpresa, String tipoDeInscricaoTomador, String inscricaoDoTomador, String nomeDoTomador, String logradouro, String bairro, String cep, String cidade, String unidadeDaFederacao, String telefone, String cnae, String codigoDeCentralizacao, String valorDaMulta, String brancos) {
        this.ordenacao = ordenacao;
        this.tipoDeInscricaoEmpresa = tipoDeInscricaoEmpresa;
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
        this.zeros = zeros;
        this.nomeEmpresa = nomeEmpresa;
        this.tipoDeInscricaoTomador = tipoDeInscricaoTomador;
        this.inscricaoDoTomador = inscricaoDoTomador;
        this.nomeDoTomador = nomeDoTomador;
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cep = cep;
        this.cidade = cidade;
        this.unidadeDaFederacao = unidadeDaFederacao;
        this.telefone = telefone;
        this.cnae = cnae;
        this.codigoDeCentralizacao = codigoDeCentralizacao;
        this.valorDaMulta = valorDaMulta;
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

    public String getCnae() {
        return cnae;
    }

    public void setCnae(String cnae) {
        this.cnae = cnae;
    }

    public String getCodigoDeCentralizacao() {
        return codigoDeCentralizacao;
    }

    public void setCodigoDeCentralizacao(String codigoDeCentralizacao) {
        this.codigoDeCentralizacao = codigoDeCentralizacao;
    }

    public String getInscricaoDaEmpresa() {
        return inscricaoDaEmpresa;
    }

    public void setInscricaoDaEmpresa(String inscricaoDaEmpresa) {
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
    }

    public String getInscricaoDoTomador() {
        return inscricaoDoTomador;
    }

    public void setInscricaoDoTomador(String inscricaoDoTomador) {
        this.inscricaoDoTomador = inscricaoDoTomador;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNomeDoTomador() {
        return nomeDoTomador;
    }

    public void setNomeDoTomador(String nomeDoTomador) {
        this.nomeDoTomador = nomeDoTomador;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipoDeInscricaoEmpresa() {
        return tipoDeInscricaoEmpresa;
    }

    public void setTipoDeInscricaoEmpresa(String tipoDeInscricaoEmpresa) {
        this.tipoDeInscricaoEmpresa = tipoDeInscricaoEmpresa;
    }

    public String getTipoDeInscricaoTomador() {
        return tipoDeInscricaoTomador;
    }

    public void setTipoDeInscricaoTomador(String tipoDeInscricaoTomador) {
        this.tipoDeInscricaoTomador = tipoDeInscricaoTomador;
    }

    public String getUnidadeDaFederacao() {
        return unidadeDaFederacao;
    }

    public void setUnidadeDaFederacao(String unidadeDaFederacao) {
        this.unidadeDaFederacao = unidadeDaFederacao;
    }

    public String getValorDaMulta() {
        return valorDaMulta;
    }

    public void setValorDaMulta(String valorDaMulta) {
        this.valorDaMulta = valorDaMulta;
    }

    public String getZeros() {
        return zeros;
    }

    public void setZeros(String zeros) {
        this.zeros = zeros;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public String getRegistroTipo50() {
        StringBuilder texto = new StringBuilder("");
        //Ordenacão, é removido posteriormente
        texto.append(ordenacao);
        //Tipo de Registro - 2 Posições
        texto.append(tipoDeRegistro);
        //Tipo de Inscrição - 1 Posição
        texto.append(StringUtils.rightPad(tipoDeInscricaoEmpresa, 1, " "));
        //Inscricao da Empresa - 14 Posições
        texto.append(StringUtils.rightPad(inscricaoDaEmpresa.replace(".", "").replace("/", ""), 14, " "));
        //Zeros - 36 Posições
        texto.append(StringUtils.rightPad(zeros, 36, "0"));
        //Nome Empresa/Razão Social - 40 Posições
        texto.append(StringUtils.rightPad(nomeEmpresa, 40, " "));
        //Tipo de Inscricao do Tomador - 1 Posição
        texto.append(StringUtils.rightPad(tipoDeInscricaoTomador, 1, " "));
        //Inscrição do Tomador - 14 posições
        texto.append(StringUtils.rightPad(inscricaoDoTomador, 14, " "));
        //Nome do Tomador - 40 Posições
        texto.append(StringUtils.rightPad(nomeDoTomador, 40, " "));
        //Logradouro - 50 Posições
        texto.append(StringUtils.rightPad(logradouro, 50, " "));
        //Bairro - 20 Posições
        texto.append(StringUtils.rightPad(bairro, 20, " "));
        //CEP - 8 Posições
        texto.append(StringUtils.rightPad(cep, 8, " "));
        //Cidade - 20 Posições
        texto.append(StringUtils.rightPad(cidade, 20, " "));
        //Unidade da Federacao - 2 posições
        texto.append(StringUtils.rightPad(unidadeDaFederacao, 2, " "));
        //Telefone - 12 Posições
        texto.append(StringUtils.rightPad(telefone, 12, " "));
        //CNAE - 7 Posicoes
        texto.append(StringUtils.rightPad(cnae, 7, " "));
        //Código de Centralização - 1 Posição
        texto.append(StringUtils.rightPad(codigoDeCentralizacao, 1, " "));
        //Valor Da Multa - 15 posições
        texto.append(StringUtils.leftPad(valorDaMulta, 15, "0"));
        //Brancos - 76 Posições
        texto.append(StringUtils.rightPad(brancos, 76, " "));
        //Final de Linha - 1 Posição
        texto.append(StringUtils.rightPad(finalDeLinha, 1, "*"));
        texto.append("\r\n");
        return texto.toString();
    }
}
