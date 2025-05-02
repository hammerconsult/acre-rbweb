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
public class SefipRegistroTipo32 implements Serializable {

    private String ordenacao;
    private final String tipoDeRegistro = "32";
    private String tipoDeInscricaoEmpresa;
    private String inscricaoDaEmpresa;
    private String tipoDeInscricaoTomador;
    private String inscricaoDoTomador;
    private String pisPasepCI;
    private String dataAdmissao;
    private String categoriaTrabalhador;
    private String nomeTrabalhador;
    private String codigoDeMovimentacao;
    private String dataDeMovimentacao;
    private String indicativoDeRecolhimentoDoFGTS;
    private String brancos;
    private final String finalDeLinha = "*";

    public SefipRegistroTipo32() {
    }

    public SefipRegistroTipo32(String ordenacao, String tipoDeInscricaoEmpresa, String inscricaoDaEmpresa, String tipoDeInscricaoTomador, String inscricaoDoTomador, String pisPasepCI, String dataAdmissao, String categoriaTrabalhador, String nomeTrabalhador, String codigoDeMovimentacao, String dataDeMovimentacao, String indicativoDeRecolhimentoDoFGTS, String brancos) {
        this.ordenacao = ordenacao;
        this.tipoDeInscricaoEmpresa = tipoDeInscricaoEmpresa;
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
        this.tipoDeInscricaoTomador = tipoDeInscricaoTomador;
        this.inscricaoDoTomador = inscricaoDoTomador;
        this.pisPasepCI = pisPasepCI;
        this.dataAdmissao = dataAdmissao;
        this.categoriaTrabalhador = categoriaTrabalhador;
        this.nomeTrabalhador = nomeTrabalhador;
        this.codigoDeMovimentacao = codigoDeMovimentacao;
        this.dataDeMovimentacao = dataDeMovimentacao;
        this.indicativoDeRecolhimentoDoFGTS = indicativoDeRecolhimentoDoFGTS;
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

    public String getCodigoDeMovimentacao() {
        return codigoDeMovimentacao;
    }

    public void setCodigoDeMovimentacao(String codigoDeMovimentacao) {
        this.codigoDeMovimentacao = codigoDeMovimentacao;
    }

    public String getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(String dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public String getDataDeMovimentacao() {
        return dataDeMovimentacao;
    }

    public void setDataDeMovimentacao(String dataDeMovimentacao) {
        this.dataDeMovimentacao = dataDeMovimentacao;
    }

    public String getIndicativoDeRecolhimentoDoFGTS() {
        return indicativoDeRecolhimentoDoFGTS;
    }

    public void setIndicativoDeRecolhimentoDoFGTS(String indicativoDeRecolhimentoDoFGTS) {
        this.indicativoDeRecolhimentoDoFGTS = indicativoDeRecolhimentoDoFGTS;
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

    public String getNomeTrabalhador() {
        return nomeTrabalhador;
    }

    public void setNomeTrabalhador(String nomeTrabalhador) {
        this.nomeTrabalhador = nomeTrabalhador;
    }

    public String getPisPasepCI() {
        return pisPasepCI;
    }

    public void setPisPasepCI(String pisPasepCI) {
        this.pisPasepCI = pisPasepCI;
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

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public String getRegistroTipo32() {
        StringBuilder texto = new StringBuilder("");

        //Ordenacão, é removido posteriormente
        texto.append(ordenacao);
        //Tipo de Registro - 2 Posições
        texto.append(tipoDeRegistro);
        //Tipo de Inscrição - 1 Posição
        texto.append(StringUtil.cortaOuCompletaDireita(tipoDeInscricaoEmpresa, 1, " "));
        //Inscricao da Empresa - 14 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(inscricaoDaEmpresa, 14, " "));
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
        texto.append(StringUtil.cortaOuCompletaDireita(StringUtil.removeEspacos(nomeTrabalhador), 70, " "));
        //Código de Movimentação - 2 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(codigoDeMovimentacao, 2, " "));
        //Data de Movimentação - 8 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(dataDeMovimentacao, 8, " "));
        //Indicativo de Recolhimento do FGTS - 1 Posição
        texto.append(StringUtil.cortaOuCompletaDireita(indicativoDeRecolhimentoDoFGTS, 1, " "));
        //Brancos - 225 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(brancos, 225, " "));
        //Final de Linha - 1 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(finalDeLinha, 1, "*"));
        texto.append("\r\n");
        return texto.toString();

    }
}
