/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author andre
 */
public class SefipRegistroTipo30 implements Serializable {

    private final String tipoDeRegistro = "30";
    private final String finalDeLinha = "*";
    private String ordenacao;
    private String tipoDeInscricaoEmpresa;
    private String inscricaoDaEmpresa;
    private String tipoDeInscricaoTomador;
    private String inscricaoDoTomador;
    private String pisPasepCI;
    private String dataAdmissao;
    private String categoriaTrabalhador;
    private String nomeTrabalhador;
    private String matriculaDoEmpregado;
    private String numeroCTPS;
    private String serieCTPS;
    private String dataDeOpcao;
    private String dataNascimento;
    private String cbo;
    private String remuneracaoSem13;
    private String remuneracao13;
    private String classeDeContribuicao;
    private String ocorrencia;
    private String valorDescontadoDoSegurado;
    private String remuneracaoBaseDeCalculoDaContribuicaoPrevidenciaria;
    private String baseDeCalculo13ReferenteCompetenciaMovimento;
    private String baseDeCalculo13ReferenteGPS;
    private String brancos;

    public SefipRegistroTipo30() {
    }

    public SefipRegistroTipo30(String ordenacao, String tipoDeInscricaoEmpresa, String inscricaoDaEmpresa, String tipoDeInscricaoTomador, String inscricaoDoTomador, String pisPasepCI, String dataAdmissao, String categoriaTrabalhador, String nomeTrabalhador, String matriculaDoEmpregado, String numeroCTPS, String serieCTPS, String dataDeOpcao, String dataNascimento, String cbo, String remuneracaoSem13, String remuneracao13, String classeDeContribuicao, String ocorrencia, String valorDescontadoDoSegurado, String remuneracaoBaseDeCalculoDaContribuicaoPrevidenciaria, String baseDeCalculo13ReferenteCompetenciaMovimento, String baseDeCalculo13ReferenteGPS, String brancos) {
        this.ordenacao = ordenacao;
        this.tipoDeInscricaoEmpresa = tipoDeInscricaoEmpresa;
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
        this.tipoDeInscricaoTomador = tipoDeInscricaoTomador;
        this.inscricaoDoTomador = inscricaoDoTomador;
        this.pisPasepCI = pisPasepCI;
        this.dataAdmissao = dataAdmissao;
        this.categoriaTrabalhador = categoriaTrabalhador;
        this.nomeTrabalhador = nomeTrabalhador;
        this.matriculaDoEmpregado = matriculaDoEmpregado;
        this.numeroCTPS = numeroCTPS;
        this.serieCTPS = serieCTPS;
        this.dataDeOpcao = dataDeOpcao;
        this.dataNascimento = dataNascimento;
        this.cbo = cbo;
        this.remuneracaoSem13 = remuneracaoSem13;
        this.remuneracao13 = remuneracao13;
        this.classeDeContribuicao = classeDeContribuicao;
        this.ocorrencia = ocorrencia;
        this.valorDescontadoDoSegurado = valorDescontadoDoSegurado;
        this.remuneracaoBaseDeCalculoDaContribuicaoPrevidenciaria = remuneracaoBaseDeCalculoDaContribuicaoPrevidenciaria;
        this.baseDeCalculo13ReferenteCompetenciaMovimento = baseDeCalculo13ReferenteCompetenciaMovimento;
        this.baseDeCalculo13ReferenteGPS = baseDeCalculo13ReferenteGPS;
        this.brancos = brancos;
    }

    public String getBaseDeCalculo13ReferenteCompetenciaMovimento() {
        return baseDeCalculo13ReferenteCompetenciaMovimento;
    }

    public void setBaseDeCalculo13ReferenteCompetenciaMovimento(String baseDeCalculo13ReferenteCompetenciaMovimento) {
        this.baseDeCalculo13ReferenteCompetenciaMovimento = baseDeCalculo13ReferenteCompetenciaMovimento;
    }

    public String getBaseDeCalculo13ReferenteGPS() {
        return baseDeCalculo13ReferenteGPS;
    }

    public void setBaseDeCalculo13ReferenteGPS(String baseDeCalculo13ReferenteGPS) {
        this.baseDeCalculo13ReferenteGPS = baseDeCalculo13ReferenteGPS;
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

    public String getClasseDeContribuicao() {
        return classeDeContribuicao;
    }

    public void setClasseDeContribuicao(String classeDeContribuicao) {
        this.classeDeContribuicao = classeDeContribuicao;
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

    public String getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(String ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public String getPisPasepCI() {
        return pisPasepCI;
    }

    public void setPisPasepCI(String pisPasepCI) {
        this.pisPasepCI = pisPasepCI;
    }

    public String getRemuneracao13() {
        return remuneracao13;
    }

    public void setRemuneracao13(String remuneracao13) {
        this.remuneracao13 = remuneracao13;
    }

    public String getRemuneracaoBaseDeCalculoDaContribuicaoPrevidenciaria() {
        return remuneracaoBaseDeCalculoDaContribuicaoPrevidenciaria;
    }

    public void setRemuneracaoBaseDeCalculoDaContribuicaoPrevidenciaria(String remuneracaoBaseDeCalculoDaContribuicaoPrevidenciaria) {
        this.remuneracaoBaseDeCalculoDaContribuicaoPrevidenciaria = remuneracaoBaseDeCalculoDaContribuicaoPrevidenciaria;
    }

    public String getRemuneracaoSem13() {
        return remuneracaoSem13;
    }

    public void setRemuneracaoSem13(String remuneracaoSem13) {
        this.remuneracaoSem13 = remuneracaoSem13;
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

    public String getValorDescontadoDoSegurado() {
        return valorDescontadoDoSegurado;
    }

    public void setValorDescontadoDoSegurado(String valorDescontadoDoSegurado) {
        this.valorDescontadoDoSegurado = valorDescontadoDoSegurado;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public boolean isSalarioPositivo(){
        BigDecimal valor = new BigDecimal(remuneracaoSem13);
        return valor.compareTo(BigDecimal.ZERO) > 0;
    }

    public String getRegistroTipo30() {
        StringBuilder texto = new StringBuilder("");

        //Ordenacão, é removido posteriormente
        texto.append(ordenacao);
        //Tipo de Registro - 2 Posições
        texto.append(tipoDeRegistro);
        //Tipo de Inscrição - 1 Posição
        texto.append(StringUtil.cortaOuCompletaDireita(tipoDeInscricaoEmpresa, 1, " "));
        //Inscricao da Empresa - 14 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(inscricaoDaEmpresa.replace(".", "").replace("/", ""), 14, " "));
        //Tipo de Inscrição Tomador- 1 Posição
        texto.append(StringUtil.cortaOuCompletaDireita(tipoDeInscricaoTomador, 1, " "));
        //Inscricao do Tomador - 14 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(inscricaoDoTomador, 14, " "));
        //PIS/PASEP/CI - 11 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(pisPasepCI, 11, " "));
        //Data Admissão - 8 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(dataAdmissao, 8, " "));
        //Categoria Trabalhador - 2 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(categoriaTrabalhador, 2, "0"));
        //Nome do Trabalhador - 70 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(nomeTrabalhador, 70, " "));
        //Matrícula do Empregado - 11 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(matriculaDoEmpregado, 11, " "));
        //numero CTPS - 7 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(numeroCTPS, 7, " "));
        //Sério CTPS - 5 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(serieCTPS, 5, " "));
        //Data de Opção - 8 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(dataDeOpcao, 8, " "));
        //Data de Nascimento - 8 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(dataNascimento, 8, " "));
        //CBO - 5 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(cbo, 5, " "));
        //Remuneracao sem 13 - 15 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(remuneracaoSem13.replace(",", "").replace(".", ""), 15, "0"));
        //Remuneracao 13 - 15 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(remuneracao13.replace(",", "").replace(".", ""), 15, "0"));
        //Classe de Contribuição - 2 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(classeDeContribuicao, 2, " "));
        //Ocorrência - 2 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(ocorrencia, 2, " "));
        //Valor Descontado do Segurado - 15 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(valorDescontadoDoSegurado.replace(",", "").replace(".", ""), 15, "0"));
        //Remuneracao Base De Cálculo da Contribuição Previdênciária - 15 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(remuneracaoBaseDeCalculoDaContribuicaoPrevidenciaria.replace(",", "").replace(".", ""), 15, "0"));
        //Base de Cálculo 13 Salário Previdência Social - 15 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(baseDeCalculo13ReferenteCompetenciaMovimento.replace(",", "").replace(".", ""), 15, "0"));
        //Base de Cálculo 13 Salário Previdência Social Referente ao GPS - 15 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(baseDeCalculo13ReferenteGPS.replace(",", "").replace(".", ""), 15, "0"));
        //brancos -98 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(brancos, 98, " "));
        String s = StringUtil.removeCaracteresEspeciais(texto.toString());
        //Final de Linha - 1 Posição
        s += StringUtil.cortaOuCompletaEsquerda(finalDeLinha, 1, "*");
        s += "\r\n";
        return s;
    }

    @Override
    public String toString() {
        return "SefipRegistroTipo30{" +
            "ordenacao='" + ordenacao + '\'' +
            ", tipoDeRegistro='" + tipoDeRegistro + '\'' +
            ", tipoDeInscricaoEmpresa='" + tipoDeInscricaoEmpresa + '\'' +
            ", inscricaoDaEmpresa='" + inscricaoDaEmpresa + '\'' +
            ", tipoDeInscricaoTomador='" + tipoDeInscricaoTomador + '\'' +
            ", inscricaoDoTomador='" + inscricaoDoTomador + '\'' +
            ", pisPasepCI='" + pisPasepCI + '\'' +
            ", dataAdmissao='" + dataAdmissao + '\'' +
            ", categoriaTrabalhador='" + categoriaTrabalhador + '\'' +
            ", nomeTrabalhador='" + nomeTrabalhador + '\'' +
            ", matriculaDoEmpregado='" + matriculaDoEmpregado + '\'' +
            ", numeroCTPS='" + numeroCTPS + '\'' +
            ", serieCTPS='" + serieCTPS + '\'' +
            ", dataDeOpcao='" + dataDeOpcao + '\'' +
            ", dataNascimento='" + dataNascimento + '\'' +
            ", cbo='" + cbo + '\'' +
            ", remuneracaoSem13='" + remuneracaoSem13 + '\'' +
            ", remuneracao13='" + remuneracao13 + '\'' +
            ", classeDeContribuicao='" + classeDeContribuicao + '\'' +
            ", ocorrencia='" + ocorrencia + '\'' +
            ", valorDescontadoDoSegurado='" + valorDescontadoDoSegurado + '\'' +
            ", remuneracaoBaseDeCalculoDaContribuicaoPrevidenciaria='" + remuneracaoBaseDeCalculoDaContribuicaoPrevidenciaria + '\'' +
            ", baseDeCalculo13ReferenteCompetenciaMovimento='" + baseDeCalculo13ReferenteCompetenciaMovimento + '\'' +
            ", baseDeCalculo13ReferenteGPS='" + baseDeCalculo13ReferenteGPS + '\'' +
            ", brancos='" + brancos + '\'' +
            ", finalDeLinha='" + finalDeLinha + '\'' +
            '}';
    }
}
