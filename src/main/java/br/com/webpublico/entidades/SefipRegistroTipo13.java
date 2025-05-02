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
public class SefipRegistroTipo13 implements Serializable {

    private final String tipoDeRegistro = "13";
    private String tipoDeInscricao;
    private String inscricaoDaEmpresa;
    private String zeros;
    private String pisPasepCi;
    private String dataAdmissao;
    private String categoriaTrabalhador;
    private String matriculaDoTrabalhador;
    private String numeroCTPS;
    private String serieCTPS;
    private String nomeTrabalhador;
    private String codigoEmpresaCaixa;
    private String codigoTrabalhadorCaixa;
    private String codigoAlteracaoCadastral;
    private String novoConteudoDoCampo;
    private String brancos;
    private final String finalDeLinha = "*";

    public SefipRegistroTipo13() {
    }

    public SefipRegistroTipo13(String tipoDeInscricao, String inscricaoDaEmpresa, String zeros, String pisPasepCi, String dataAdmissao, String categoriaTrabalhador, String matriculaDoTrabalhador, String numeroCTPS, String serieCTPS, String nomeTrabalhador, String codigoEmpresaCaixa, String codigoTrabalhadorCaixa, String codigoAlteracaoCadastral, String novoConteudoDoCampo, String brancos) {
        this.tipoDeInscricao = tipoDeInscricao;
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
        this.zeros = zeros;
        this.pisPasepCi = pisPasepCi;
        this.dataAdmissao = dataAdmissao;
        this.categoriaTrabalhador = categoriaTrabalhador;
        this.matriculaDoTrabalhador = matriculaDoTrabalhador;
        this.numeroCTPS = numeroCTPS;
        this.serieCTPS = serieCTPS;
        this.nomeTrabalhador = nomeTrabalhador;
        this.codigoEmpresaCaixa = codigoEmpresaCaixa;
        this.codigoTrabalhadorCaixa = codigoTrabalhadorCaixa;
        this.codigoAlteracaoCadastral = codigoAlteracaoCadastral;
        this.novoConteudoDoCampo = novoConteudoDoCampo;
        this.brancos = brancos;
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

    public String getCodigoAlteracaoCadastral() {
        return codigoAlteracaoCadastral;
    }

    public void setCodigoAlteracaoCadastral(String codigoAlteracaoCadastral) {
        this.codigoAlteracaoCadastral = codigoAlteracaoCadastral;
    }

    public String getCodigoEmpresaCaixa() {
        return codigoEmpresaCaixa;
    }

    public void setCodigoEmpresaCaixa(String codigoEmpresaCaixa) {
        this.codigoEmpresaCaixa = codigoEmpresaCaixa;
    }

    public String getCodigoTrabalhadorCaixa() {
        return codigoTrabalhadorCaixa;
    }

    public void setCodigoTrabalhadorCaixa(String codigoTrabalhadorCaixa) {
        this.codigoTrabalhadorCaixa = codigoTrabalhadorCaixa;
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

    public String getMatriculaDoTrabalhador() {
        return matriculaDoTrabalhador;
    }

    public void setMatriculaDoTrabalhador(String matriculaDoTrabalhador) {
        this.matriculaDoTrabalhador = matriculaDoTrabalhador;
    }

    public String getNomeTrabalhador() {
        return nomeTrabalhador;
    }

    public void setNomeTrabalhador(String nomeTrabalhador) {
        this.nomeTrabalhador = nomeTrabalhador;
    }

    public String getNovoConteudoDoCampo() {
        return novoConteudoDoCampo;
    }

    public void setNovoConteudoDoCampo(String novoConteudoDoCampo) {
        this.novoConteudoDoCampo = novoConteudoDoCampo;
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

    public String getZeros() {
        return zeros;
    }

    public void setZeros(String zeros) {
        this.zeros = zeros;
    }

    public String getRegistroTipo13() {
        StringBuilder texto = new StringBuilder("");

        //Tipo de Registro - 2 Posições
        texto.append(tipoDeRegistro);
        //Tipo de Inscrição - 1 Posição
        texto.append(StringUtil.cortaOuCompletaDireita(tipoDeInscricao, 1, " "));
        //Inscrição da Empresa - 14 posições
        texto.append(StringUtil.cortaOuCompletaDireita(inscricaoDaEmpresa.replace(".", "").replace("/", ""), 14, " "));
        //Zeros - 36 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(zeros, 36, "0"));
        //PIS/PASEP/CI - 11 Posicoes
        texto.append(StringUtil.cortaOuCompletaDireita(pisPasepCi, 11, " "));
        //Data de Admissao 6 posições
        texto.append(StringUtil.cortaOuCompletaDireita(dataAdmissao, 6, " "));
        //Categoria Trabalhador - 2 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(categoriaTrabalhador, 2, "0"));
        //Matrícula do Trabalhador - 11 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(matriculaDoTrabalhador, 11, " "));
        //Número CTPS - 7 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(numeroCTPS, 7, " "));
        //Série CTPS - 5 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(serieCTPS, 5, " "));
        //Nome Trabalhador - 70 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(nomeTrabalhador, 70, " "));
        //Código Empresa CAIXA - 14 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(codigoEmpresaCaixa, 14, " "));
        //Código Trabalhador CAIXA - 11 posições
        texto.append(StringUtil.cortaOuCompletaDireita(codigoTrabalhadorCaixa, 11, " "));
        //Código Alteração cadastral - 3 posições
        texto.append(StringUtil.cortaOuCompletaDireita(codigoAlteracaoCadastral, 3, " "));
        //Novo Conteúdo do Campo
        texto.append(StringUtil.cortaOuCompletaDireita(novoConteudoDoCampo, 70, " "));
        //Brancos - 94 posições
        texto.append(StringUtil.cortaOuCompletaDireita(brancos, 94, " "));
        String s = StringUtil.removeCaracteresEspeciais(texto.toString());

        //Final de Linha - 1 Posição
        s += StringUtil.cortaOuCompletaEsquerda(finalDeLinha, 1, "*");
        s += "\r\n";
        return s;
    }
}
