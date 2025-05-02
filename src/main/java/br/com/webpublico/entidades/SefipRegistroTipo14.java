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
public class SefipRegistroTipo14 implements Serializable {

    private String ordenacao;
    private final String tipoDeRegistro = "14";
    private String tipoDeInscricao;
    private String inscricaoDaEmpresa;
    private String zeros;
    private String pisPasepCi;
    private String dataAdmissao;
    private String categoriaTrabalhador;
    private String nomeTrabalhador;
    private String numeroCTPS;
    private String serieCTPS;
    private String logradouro;
    private String bairro;
    private String cep;
    private String cidade;
    private String unidadeDaFederação;
    private String brancos;
    private final String finalDeLinha = "*";

    public SefipRegistroTipo14() {
    }

    public SefipRegistroTipo14(String ordenacao, String tipoDeInscricao, String inscricaoDaEmpresa, String zeros, String pisPasepCi, String dataAdmissao, String categoriaTrabalhador, String nomeTrabalhador, String numeroCTPS, String serieCTPS, String logradouro, String bairro, String cep, String cidade, String unidadeDaFederação, String brancos) {
        this.ordenacao = ordenacao;
        this.tipoDeInscricao = tipoDeInscricao;
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
        this.zeros = zeros;
        this.pisPasepCi = pisPasepCi;
        this.dataAdmissao = dataAdmissao;
        this.categoriaTrabalhador = categoriaTrabalhador;
        this.nomeTrabalhador = nomeTrabalhador;
        this.numeroCTPS = numeroCTPS;
        this.serieCTPS = serieCTPS;
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cep = cep;
        this.cidade = cidade;
        this.unidadeDaFederação = unidadeDaFederação;
        this.brancos = brancos;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
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

    public String getCategoriaTrabalhador() {
        return categoriaTrabalhador;
    }

    public void setCategoriaTrabalhador(String categoriaTrabalhador) {
        this.categoriaTrabalhador = categoriaTrabalhador;
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

    public String getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(String dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public String getInscricaoDaEmpresa() {
        return inscricaoDaEmpresa;
    }

    public void setInscricaoDaEmpresa(String inscricaoDaEmpresa) {
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNomeTrabalhador() {
        return nomeTrabalhador;
    }

    public void setNomeTrabalhador(String nomeTrabalhador) {
        this.nomeTrabalhador = nomeTrabalhador;
    }

    public String getNumeroCTPS() {
        return numeroCTPS;
    }

    public void setNumeroCTPS(String numeroCTPS) {
        this.numeroCTPS = numeroCTPS;
    }

    public String getPisPasepCi() {
        return pisPasepCi;
    }

    public void setPisPasepCi(String pisPasepCi) {
        this.pisPasepCi = pisPasepCi;
    }

    public String getSerieCTPS() {
        return serieCTPS;
    }

    public void setSerieCTPS(String serieCTPS) {
        this.serieCTPS = serieCTPS;
    }

    public String getTipoDeInscricao() {
        return tipoDeInscricao;
    }

    public void setTipoDeInscricao(String tipoDeInscricao) {
        this.tipoDeInscricao = tipoDeInscricao;
    }

    public String getUnidadeDaFederação() {
        return unidadeDaFederação;
    }

    public void setUnidadeDaFederação(String unidadeDaFederação) {
        this.unidadeDaFederação = unidadeDaFederação;
    }

    public String getZeros() {
        return zeros;
    }

    public void setZeros(String zeros) {
        this.zeros = zeros;
    }

    public String getRegistroTipo14() {
        StringBuilder texto = new StringBuilder("");

        //Ordenacão, é removido posteriormente
        texto.append(ordenacao);
        //Tipo de Registro - 2 posições
        texto.append(tipoDeRegistro);
        //Tipo de Inscrição - Empresa - 1 Posição
        texto.append(StringUtil.cortaOuCompletaDireita(tipoDeInscricao, 1, " "));
        //Inscrição da Empresa - 14 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(inscricaoDaEmpresa.replace(".", "").replace("/", ""), 14, " "));
        //Zeros - 36 posições
        texto.append(StringUtil.cortaOuCompletaDireita(zeros, 36, "0"));
        //PIS/PASEP/CI - 11 posições
        texto.append(StringUtil.cortaOuCompletaDireita(pisPasepCi, 11, " "));
        //Data Admissão - 8 posicoes
        texto.append(StringUtil.cortaOuCompletaDireita(dataAdmissao, 8, " "));
        //Categoria Trabalhador - 2 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(categoriaTrabalhador, 2, " "));
        //Nome Trabalhador - 70 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(nomeTrabalhador, 70, " "));
        //Número CTPS - 7 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(numeroCTPS, 7, " "));
        //Série CTPS - 5 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(serieCTPS, 5, " "));
        //logradouro - 50 posições
        texto.append(StringUtil.cortaOuCompletaDireita(logradouro, 50, " "));
        //bairro - 20 posições
        texto.append(StringUtil.cortaOuCompletaDireita(bairro, 20, " "));
        //cep - 8 posições
        texto.append(StringUtil.cortaOuCompletaDireita(cep.replace("-", ""), 8, " "));
        //Cidade - 20 posições
        texto.append(StringUtil.cortaOuCompletaDireita(cidade, 20, " "));
        //Unidade da Federação - 2 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(unidadeDaFederação, 2, " "));
        //Brancos - 103 posições
        texto.append(StringUtil.cortaOuCompletaDireita(brancos, 103, " "));

        String s = StringUtil.removeCaracteresEspeciais(texto.toString());

        //Final de Linha - 1 Posição
        s += StringUtil.cortaOuCompletaEsquerda(finalDeLinha, 1, "*");
        s += "\r\n";
        return s;
    }
}
