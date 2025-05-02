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
public class SefipRegistroTipo21 implements Serializable {

    private String ordenacao;
    private final String tipoDeRegistro = "21";
    private String tipoDeInscricao;
    private String inscricaoDaEmpresa;
    private String tipoInscricaoTomador;
    private String inscricaoTomador;
    private String zeros;
    private String compensacaoValorCorrigido;
    private String compensacaoPeriodoInicio;
    private String compensacaoPeriodoFim;
    private String recolhimentoCompetenciasAnterioresValorINSS;
    private String recolhimentoCompetenciasAnterioresOutrasEntidades;
    private String parcelamentoDoFGTSCategorias01;
    private String parcelamentoDoFGTSCategorias02;
    private String parcelamentoDoFGTSValorRecolhido;
    private String brancos;
    private final String finalDeLinha = "*";

    public SefipRegistroTipo21() {
    }

    public SefipRegistroTipo21(String ordenacao, String tipoDeInscricao, String inscricaoDaEmpresa, String tipoInscricaoTomador, String inscricaoTomador, String zeros, String compensacaoValorCorrigido, String compensacaoPeriodoInicio, String compensacaoPeriodoFim, String recolhimentoCompetenciasAnterioresValorINSS, String recolhimentoCompetenciasAnterioresOutrasEntidades, String parcelamentoDoFGTSCategorias01, String parcelamentoDoFGTSCategorias02, String parcelamentoDoFGTSValorRecolhido, String brancos) {
        this.ordenacao = ordenacao;
        this.tipoDeInscricao = tipoDeInscricao;
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
        this.tipoInscricaoTomador = tipoInscricaoTomador;
        this.inscricaoTomador = inscricaoTomador;
        this.zeros = zeros;
        this.compensacaoValorCorrigido = compensacaoValorCorrigido;
        this.compensacaoPeriodoInicio = compensacaoPeriodoInicio;
        this.compensacaoPeriodoFim = compensacaoPeriodoFim;
        this.recolhimentoCompetenciasAnterioresValorINSS = recolhimentoCompetenciasAnterioresValorINSS;
        this.recolhimentoCompetenciasAnterioresOutrasEntidades = recolhimentoCompetenciasAnterioresOutrasEntidades;
        this.parcelamentoDoFGTSCategorias01 = parcelamentoDoFGTSCategorias01;
        this.parcelamentoDoFGTSCategorias02 = parcelamentoDoFGTSCategorias02;
        this.parcelamentoDoFGTSValorRecolhido = parcelamentoDoFGTSValorRecolhido;
        this.brancos = brancos;
    }

    public String getBrancos() {
        return brancos;
    }

    public void setBrancos(String brancos) {
        this.brancos = brancos;
    }

    public String getCompensacaoPeriodoFim() {
        return compensacaoPeriodoFim;
    }

    public void setCompensacaoPeriodoFim(String compensacaoPeriodoFim) {
        this.compensacaoPeriodoFim = compensacaoPeriodoFim;
    }

    public String getCompensacaoPeriodoInicio() {
        return compensacaoPeriodoInicio;
    }

    public void setCompensacaoPeriodoInicio(String compensacaoPeriodoInicio) {
        this.compensacaoPeriodoInicio = compensacaoPeriodoInicio;
    }

    public String getCompensacaoValorCorrigido() {
        return compensacaoValorCorrigido;
    }

    public void setCompensacaoValorCorrigido(String compensacaoValorCorrigido) {
        this.compensacaoValorCorrigido = compensacaoValorCorrigido;
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

    public String getParcelamentoDoFGTSCategorias01() {
        return parcelamentoDoFGTSCategorias01;
    }

    public void setParcelamentoDoFGTSCategorias01(String parcelamentoDoFGTSCategorias01) {
        this.parcelamentoDoFGTSCategorias01 = parcelamentoDoFGTSCategorias01;
    }

    public String getParcelamentoDoFGTSCategorias02() {
        return parcelamentoDoFGTSCategorias02;
    }

    public void setParcelamentoDoFGTSCategorias02(String parcelamentoDoFGTSCategorias02) {
        this.parcelamentoDoFGTSCategorias02 = parcelamentoDoFGTSCategorias02;
    }

    public String getParcelamentoDoFGTSValorRecolhido() {
        return parcelamentoDoFGTSValorRecolhido;
    }

    public void setParcelamentoDoFGTSValorRecolhido(String parcelamentoDoFGTSValorRecolhido) {
        this.parcelamentoDoFGTSValorRecolhido = parcelamentoDoFGTSValorRecolhido;
    }

    public String getRecolhimentoCompetenciasAnterioresOutrasEntidades() {
        return recolhimentoCompetenciasAnterioresOutrasEntidades;
    }

    public void setRecolhimentoCompetenciasAnterioresOutrasEntidades(String recolhimentoCompetenciasAnterioresOutrasEntidades) {
        this.recolhimentoCompetenciasAnterioresOutrasEntidades = recolhimentoCompetenciasAnterioresOutrasEntidades;
    }

    public String getRecolhimentoCompetenciasAnterioresValorINSS() {
        return recolhimentoCompetenciasAnterioresValorINSS;
    }

    public void setRecolhimentoCompetenciasAnterioresValorINSS(String recolhimentoCompetenciasAnterioresValorINSS) {
        this.recolhimentoCompetenciasAnterioresValorINSS = recolhimentoCompetenciasAnterioresValorINSS;
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

    public String getRegistro21() {
        StringBuilder texto = new StringBuilder("");
        //Ordenacão, é removido posteriormente
        texto.append(ordenacao);
        //Tipo de Registro - 2 Posições
        texto.append(tipoDeRegistro);
        //Tipo de Inscrição - 1 Posição
        texto.append(StringUtil.cortaOuCompletaDireita(tipoDeInscricao, 1, " "));
        //Inscricao da Empresa - 14 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(inscricaoDaEmpresa, 14, " "));
        //Tipo de Inscrição Tomador- 1 Posição
        texto.append(StringUtil.cortaOuCompletaDireita(tipoInscricaoTomador, 1, " "));
        //Inscricao do Tomador - 14 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(inscricaoTomador, 14, " "));
        //Zeros - 21 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(zeros, 21, "0"));
        //Compensação Valor Corrigido - 15 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(compensacaoValorCorrigido, 15, "0"));
        //Compensação Periodo Inicio - 6 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(compensacaoPeriodoInicio, 6, " "));
        //Compensação Periodo Fim - 6 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(compensacaoPeriodoFim, 6, " "));
        //Recolhimento de Competências Anteriores - Valor do INSS sobre folha de Pagamento - 15 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(recolhimentoCompetenciasAnterioresValorINSS, 15, "0"));
        //Recolhimento de Competências Anteriores - Outras Entidades sobre folha de Pagamento. - 15 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(recolhimentoCompetenciasAnterioresOutrasEntidades, 51, "0"));
        //Parcelamento do FGTS Categoria 01,02,03,05 e 06 - 15 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(parcelamentoDoFGTSCategorias01, 15, "0"));
        //Parcelamento do FGTS Categoria 04 e 07 - 15 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(parcelamentoDoFGTSCategorias02, 15, "0"));
        //Parcelamento do FGTS Valor Recolihdo - 15 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(parcelamentoDoFGTSValorRecolhido, 15, "0"));
        //Brancos - 204 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(brancos, 204, " "));
        //Final de Linha - 1 Posição
        texto.append(StringUtil.cortaOuCompletaEsquerda(finalDeLinha, 1, "*"));
        texto.append("\n");
        return texto.toString();
    }
}
