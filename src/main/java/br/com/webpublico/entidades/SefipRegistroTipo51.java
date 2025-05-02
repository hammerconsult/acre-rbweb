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
public class SefipRegistroTipo51 implements Serializable {

    private String ordenacao;
    private final String tipoDeRegistro = "51";
    private String tipoDeInscricaoEmpresa;
    private String inscricaoDaEmpresa;
    private String tipoDeInscricaoTomador;
    private String inscricaoDoTomador;
    private String pisPasep;
    private String dataAdmissao;
    private String categoriaTrabalhador;
    private String nomeTrabalhador;
    private String matriculaDoEmpregado;
    private String numeroCTPS;
    private String serieCTPS;
    private String dataDeOpcao;
    private String dataNascimento;
    private String cbo;
    private String valorDepositoSem13;
    private String valorDepositoSobre13;
    private String valorDoJAM;
    private String brancos;
    private final String finalDeLinha = "*";

    public SefipRegistroTipo51() {
    }

    public SefipRegistroTipo51(String ordenacao, String tipoDeInscricaoEmpresa, String inscricaoDaEmpresa, String tipoDeInscricaoTomador, String inscricaoDoTomador, String pisPasep, String dataAdmissao, String categoriaTrabalhador, String nomeTrabalhador, String matriculaDoEmpregado, String numeroCTPS, String serieCTPS, String dataDeOpcao, String dataNascimento, String cbo, String valorDepositoSem13, String valorDepositoSobre13, String valorDoJAM, String brancos) {
        this.ordenacao = ordenacao;
        this.tipoDeInscricaoEmpresa = tipoDeInscricaoEmpresa;
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
        this.tipoDeInscricaoTomador = tipoDeInscricaoTomador;
        this.inscricaoDoTomador = inscricaoDoTomador;
        this.pisPasep = pisPasep;
        this.dataAdmissao = dataAdmissao;
        this.categoriaTrabalhador = categoriaTrabalhador;
        this.nomeTrabalhador = nomeTrabalhador;
        this.matriculaDoEmpregado = matriculaDoEmpregado;
        this.numeroCTPS = numeroCTPS;
        this.serieCTPS = serieCTPS;
        this.dataDeOpcao = dataDeOpcao;
        this.dataNascimento = dataNascimento;
        this.cbo = cbo;
        this.valorDepositoSem13 = valorDepositoSem13;
        this.valorDepositoSobre13 = valorDepositoSobre13;
        this.valorDoJAM = valorDoJAM;
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

    public String getCbo() {
        return cbo;
    }

    public void setCbo(String cbo) {
        this.cbo = cbo;
    }

    public String getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(String dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public String getDataDeOpcao() {
        return dataDeOpcao;
    }

    public void setDataDeOpcao(String dataDeOpcao) {
        this.dataDeOpcao = dataDeOpcao;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
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

    public String getMatriculaDoEmpregado() {
        return matriculaDoEmpregado;
    }

    public void setMatriculaDoEmpregado(String matriculaDoEmpregado) {
        this.matriculaDoEmpregado = matriculaDoEmpregado;
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

    public String getPisPasep() {
        return pisPasep;
    }

    public void setPisPasep(String pisPasep) {
        this.pisPasep = pisPasep;
    }

    public String getSerieCTPS() {
        return serieCTPS;
    }

    public void setSerieCTPS(String serieCTPS) {
        this.serieCTPS = serieCTPS;
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

    public String getValorDepositoSem13() {
        return valorDepositoSem13;
    }

    public void setValorDepositoSem13(String valorDepositoSem13) {
        this.valorDepositoSem13 = valorDepositoSem13;
    }

    public String getValorDepositoSobre13() {
        return valorDepositoSobre13;
    }

    public void setValorDepositoSobre13(String valorDepositoSobre13) {
        this.valorDepositoSobre13 = valorDepositoSobre13;
    }

    public String getValorDoJAM() {
        return valorDoJAM;
    }

    public void setValorDoJAM(String valorDoJAM) {
        this.valorDoJAM = valorDoJAM;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public String getRegistroTipo51() {
        StringBuilder texto = new StringBuilder("");

        //Ordenacão, é removido posteriormente
        texto.append(ordenacao);
        //Tipo de Registro - 2 Posições
        texto.append(tipoDeRegistro);
        //Tipo de Inscrição - 1 Posição
        texto.append(StringUtils.rightPad(tipoDeInscricaoEmpresa, 1, " "));
        //Inscricao da Empresa - 14 Posições
        texto.append(StringUtils.rightPad(inscricaoDaEmpresa.replace(".", "").replace("/", ""), 14, " "));
        //Tipo de Inscrição Tomador- 1 Posição
        texto.append(StringUtils.rightPad(tipoDeInscricaoTomador, 1, " "));
        //Inscricao do Tomador - 14 Posições
        texto.append(StringUtils.rightPad(inscricaoDoTomador, 14, " "));
        //PIS/PASEP - 11 Posições
        texto.append(StringUtils.rightPad(pisPasep, 11, " "));
        //Data Admissão - 8 Posições
        texto.append(StringUtils.rightPad(dataAdmissao, 8, " "));
        //Categoria Trabalhador - 2 Posições
        texto.append(StringUtils.rightPad(categoriaTrabalhador, 2, " "));
        //Nome do Trabalhador - 70 Posições
        texto.append(StringUtils.rightPad(nomeTrabalhador, 70, " "));
        //Matrícula do Empregado - 11 Posições
        texto.append(StringUtils.rightPad(matriculaDoEmpregado, 11, " "));
        //numero CTPS - 7 Posições
        texto.append(StringUtils.rightPad(numeroCTPS, 7, " "));
        //Sério CTPS - 5 Posições
        texto.append(StringUtils.rightPad(serieCTPS, 5, " "));
        //Data de Opção - 8 Posições
        texto.append(StringUtils.rightPad(dataDeOpcao, 8, " "));
        //Data de Nascimento - 8 Posições
        texto.append(StringUtils.rightPad(dataNascimento, 8, " "));
        //CBO - 5 Posições
        texto.append(StringUtils.rightPad(cbo, 5, " "));
        //Valor do Depósito sem 13 salario - 15 posicoes
        texto.append(StringUtils.leftPad(valorDepositoSem13, 15, "0"));
        //Valor do Depósito sobre 13 salario - 15 posicoes
        texto.append(StringUtils.leftPad(valorDepositoSobre13, 15, "0"));
        //Valor do JAM - 15 posições
        texto.append(StringUtils.leftPad(valorDoJAM, 15, "0"));
        //Brancos - 147 posições
        texto.append(StringUtils.leftPad(brancos, 147, " "));
        //Final de Linha - 1 Posição
        texto.append(StringUtils.leftPad(finalDeLinha, 1, "*"));
        texto.append("\r\n");
        return texto.toString();
    }
}
