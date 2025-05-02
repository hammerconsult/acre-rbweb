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
public class SefipRegistroTipo12 implements Serializable {

    private String ordenacao;
    private final String tipoDeRegistro = "12";
    private String tipoDeInscricao;
    private String inscricaoDaEmpresa;
    private String zeros;
    private String deducao13SalarioLicencaMaternidade;
    private String receitaEventoDesportivoPatrocinio;
    private String indicativoOrigemDaReceita;
    private String comercializacaoDaProducaoPF;
    private String comercializacaoDaProducaoPJ;
    private String outrasInformacoesProcesso;
    private String outrasInformacoesProcessoAno;
    private String outrasInformacoesVara;
    private String outrasInformacoesPeriodoInicio;
    private String outrasInformacoesPeriodoFim;
    private String compensacaoValorCorrigido;
    private String compensacaoPeriodoInicio;
    private String compensacaoPeriodoFim;
    private String recolhimentoCompetenciasAnterioresValorINSSFP;
    private String recolhimentoCompetenciasAnterioresOutrasFP;
    private String recolhimentoCompetenciasAnterioresComercializacaoProducaoINSS;
    private String recolhimentoCompetenciasAnterioresComercializacaoProducaoOutras;
    private String recolhimentoCompetenciasAnterioresEventoDesportivoPatrocinioINSS;
    private String parcelamentoFGTSCategorias1;
    private String parcelamentoFGTSCategorias2;
    private String parcelamentoFGTSValorRecolhido;
    private String valoresPagosCooperativosTrabalho;
    private String implementacaoFutura;
    private String brancos;
    private String finalDeLinha = "*";

    public SefipRegistroTipo12() {
    }

    public SefipRegistroTipo12(String ordenacao, String tipoDeInscricao, String inscricaoDaEmpresa, String zeros, String deducao13SalarioLicencaMaternidade, String receitaEventoDesportivoPatrocinio, String indicativoOrigemDaReceita, String comercializacaoDaProducaoPF, String comercializacaoDaProducaoPJ, String outrasInformacoesProcesso, String outrasInformacoesProcessoAno, String outrasInformacoesVara, String outrasInformacoesPeriodoInicio, String outrasInformacoesPeriodoFim, String compensacaoValorCorrigido, String compensacaoPeriodoInicio, String compensacaoPeriodoFim, String recolhimentoCompetenciasAnterioresValorINSSFP, String recolhimentoCompetenciasAnterioresOutrasFP, String recolhimentoCompetenciasAnterioresComercializacaoProducaoINSS, String recolhimentoCompetenciasAnterioresComercializacaoProducaoOutras, String recolhimentoCompetenciasAnterioresEventoDesportivoPatrocinioINSS, String parcelamentoFGTSCategorias1, String parcelamentoFGTSCategorias2, String parcelamentoFGTSValorRecolhido, String valoresPagosCooperativosTrabalho, String implementacaoFutura, String brancos) {
        this.ordenacao = ordenacao;
        this.tipoDeInscricao = tipoDeInscricao;
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
        this.zeros = zeros;
        this.deducao13SalarioLicencaMaternidade = deducao13SalarioLicencaMaternidade;
        this.receitaEventoDesportivoPatrocinio = receitaEventoDesportivoPatrocinio;
        this.indicativoOrigemDaReceita = indicativoOrigemDaReceita;
        this.comercializacaoDaProducaoPF = comercializacaoDaProducaoPF;
        this.comercializacaoDaProducaoPJ = comercializacaoDaProducaoPJ;
        this.outrasInformacoesProcesso = outrasInformacoesProcesso;
        this.outrasInformacoesProcessoAno = outrasInformacoesProcessoAno;
        this.outrasInformacoesVara = outrasInformacoesVara;
        this.outrasInformacoesPeriodoInicio = outrasInformacoesPeriodoInicio;
        this.outrasInformacoesPeriodoFim = outrasInformacoesPeriodoFim;
        this.compensacaoValorCorrigido = compensacaoValorCorrigido;
        this.compensacaoPeriodoInicio = compensacaoPeriodoInicio;
        this.compensacaoPeriodoFim = compensacaoPeriodoFim;
        this.recolhimentoCompetenciasAnterioresValorINSSFP = recolhimentoCompetenciasAnterioresValorINSSFP;
        this.recolhimentoCompetenciasAnterioresOutrasFP = recolhimentoCompetenciasAnterioresOutrasFP;
        this.recolhimentoCompetenciasAnterioresComercializacaoProducaoINSS = recolhimentoCompetenciasAnterioresComercializacaoProducaoINSS;
        this.recolhimentoCompetenciasAnterioresComercializacaoProducaoOutras = recolhimentoCompetenciasAnterioresComercializacaoProducaoOutras;
        this.recolhimentoCompetenciasAnterioresEventoDesportivoPatrocinioINSS = recolhimentoCompetenciasAnterioresEventoDesportivoPatrocinioINSS;
        this.parcelamentoFGTSCategorias1 = parcelamentoFGTSCategorias1;
        this.parcelamentoFGTSCategorias2 = parcelamentoFGTSCategorias2;
        this.parcelamentoFGTSValorRecolhido = parcelamentoFGTSValorRecolhido;
        this.valoresPagosCooperativosTrabalho = valoresPagosCooperativosTrabalho;
        this.implementacaoFutura = implementacaoFutura;
        this.brancos = brancos;
    }

    public String getBrancos() {
        return brancos;
    }

    public void setBrancos(String brancos) {
        this.brancos = brancos;
    }

    public String getComercializacaoDaProducaoPF() {
        return comercializacaoDaProducaoPF;
    }

    public void setComercializacaoDaProducaoPF(String comercializacaoDaProducaoPF) {
        this.comercializacaoDaProducaoPF = comercializacaoDaProducaoPF;
    }

    public String getComercializacaoDaProducaoPJ() {
        return comercializacaoDaProducaoPJ;
    }

    public void setComercializacaoDaProducaoPJ(String comercializacaoDaProducaoPJ) {
        this.comercializacaoDaProducaoPJ = comercializacaoDaProducaoPJ;
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

    public String getDeducao13SalarioLicencaMaternidade() {
        return deducao13SalarioLicencaMaternidade;
    }

    public void setDeducao13SalarioLicencaMaternidade(String deducao13SalarioLicencaMaternidade) {
        this.deducao13SalarioLicencaMaternidade = deducao13SalarioLicencaMaternidade;
    }

    public String getFinalDeLinha() {
        return finalDeLinha;
    }

    public void setFinalDeLinha(String finalDeLinha) {
        this.finalDeLinha = finalDeLinha;
    }

    public String getImplementacaoFutura() {
        return implementacaoFutura;
    }

    public void setImplementacaoFutura(String implementacaoFutura) {
        this.implementacaoFutura = implementacaoFutura;
    }

    public String getIndicativoOrigemDaReceita() {
        return indicativoOrigemDaReceita;
    }

    public void setIndicativoOrigemDaReceita(String indicativoOrigemDaReceita) {
        this.indicativoOrigemDaReceita = indicativoOrigemDaReceita;
    }

    public String getInscricaoDaEmpresa() {
        return inscricaoDaEmpresa;
    }

    public void setInscricaoDaEmpresa(String inscricaoDaEmpresa) {
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
    }

    public String getOutrasInformacoesPeriodoInicio() {
        return outrasInformacoesPeriodoInicio;
    }

    public void setOutrasInformacoesPeriodoInicio(String outrasInformacoesPeriodoInicio) {
        this.outrasInformacoesPeriodoInicio = outrasInformacoesPeriodoInicio;
    }

    public String getOutrasInformacoesPeriodoFim() {
        return outrasInformacoesPeriodoFim;
    }

    public void setOutrasInformacoesPeriodoFim(String outrasInformacoesPeriodoFim) {
        this.outrasInformacoesPeriodoFim = outrasInformacoesPeriodoFim;
    }

    public String getOutrasInformacoesProcesso() {
        return outrasInformacoesProcesso;
    }

    public void setOutrasInformacoesProcesso(String outrasInformacoesProcesso) {
        this.outrasInformacoesProcesso = outrasInformacoesProcesso;
    }

    public String getOutrasInformacoesProcessoAno() {
        return outrasInformacoesProcessoAno;
    }

    public void setOutrasInformacoesProcessoAno(String outrasInformacoesProcessoAno) {
        this.outrasInformacoesProcessoAno = outrasInformacoesProcessoAno;
    }

    public String getOutrasInformacoesVara() {
        return outrasInformacoesVara;
    }

    public void setOutrasInformacoesVara(String outrasInformacoesVara) {
        this.outrasInformacoesVara = outrasInformacoesVara;
    }

    public String getParcelamentoFGTSCategorias1() {
        return parcelamentoFGTSCategorias1;
    }

    public void setParcelamentoFGTSCategorias1(String parcelamentoFGTSCategorias1) {
        this.parcelamentoFGTSCategorias1 = parcelamentoFGTSCategorias1;
    }

    public String getParcelamentoFGTSCategorias2() {
        return parcelamentoFGTSCategorias2;
    }

    public void setParcelamentoFGTSCategorias2(String parcelamentoFGTSCategorias2) {
        this.parcelamentoFGTSCategorias2 = parcelamentoFGTSCategorias2;
    }

    public String getParcelamentoFGTSValorRecolhido() {
        return parcelamentoFGTSValorRecolhido;
    }

    public void setParcelamentoFGTSValorRecolhido(String parcelamentoFGTSValorRecolhido) {
        this.parcelamentoFGTSValorRecolhido = parcelamentoFGTSValorRecolhido;
    }

    public String getReceitaEventoDesportivoPatrocinio() {
        return receitaEventoDesportivoPatrocinio;
    }

    public void setReceitaEventoDesportivoPatrocinio(String receitaEventoDesportivoPatrocinio) {
        this.receitaEventoDesportivoPatrocinio = receitaEventoDesportivoPatrocinio;
    }

    public String getRecolhimentoCompetenciasAnterioresComercializacaoProducaoINSS() {
        return recolhimentoCompetenciasAnterioresComercializacaoProducaoINSS;
    }

    public void setRecolhimentoCompetenciasAnterioresComercializacaoProducaoINSS(String recolhimentoCompetenciasAnterioresComercializacaoProducaoINSS) {
        this.recolhimentoCompetenciasAnterioresComercializacaoProducaoINSS = recolhimentoCompetenciasAnterioresComercializacaoProducaoINSS;
    }

    public String getRecolhimentoCompetenciasAnterioresComercializacaoProducaoOutras() {
        return recolhimentoCompetenciasAnterioresComercializacaoProducaoOutras;
    }

    public void setRecolhimentoCompetenciasAnterioresComercializacaoProducaoOutras(String recolhimentoCompetenciasAnterioresComercializacaoProducaoOutras) {
        this.recolhimentoCompetenciasAnterioresComercializacaoProducaoOutras = recolhimentoCompetenciasAnterioresComercializacaoProducaoOutras;
    }

    public String getRecolhimentoCompetenciasAnterioresEventoDesportivoPatrocinioINSS() {
        return recolhimentoCompetenciasAnterioresEventoDesportivoPatrocinioINSS;
    }

    public void setRecolhimentoCompetenciasAnterioresEventoDesportivoPatrocinioINSS(String recolhimentoCompetenciasAnterioresEventoDesportivoPatrocinioINSS) {
        this.recolhimentoCompetenciasAnterioresEventoDesportivoPatrocinioINSS = recolhimentoCompetenciasAnterioresEventoDesportivoPatrocinioINSS;
    }

    public String getRecolhimentoCompetenciasAnterioresOutrasFP() {
        return recolhimentoCompetenciasAnterioresOutrasFP;
    }

    public void setRecolhimentoCompetenciasAnterioresOutrasFP(String recolhimentoCompetenciasAnterioresOutrasFP) {
        this.recolhimentoCompetenciasAnterioresOutrasFP = recolhimentoCompetenciasAnterioresOutrasFP;
    }

    public String getRecolhimentoCompetenciasAnterioresValorINSSFP() {
        return recolhimentoCompetenciasAnterioresValorINSSFP;
    }

    public void setRecolhimentoCompetenciasAnterioresValorINSSFP(String recolhimentoCompetenciasAnterioresValorINSSFP) {
        this.recolhimentoCompetenciasAnterioresValorINSSFP = recolhimentoCompetenciasAnterioresValorINSSFP;
    }

    public String getTipoDeInscricao() {
        return tipoDeInscricao;
    }

    public void setTipoDeInscricao(String tipoDeInscricao) {
        this.tipoDeInscricao = tipoDeInscricao;
    }

    public String getValoresPagosCooperativosTrabalho() {
        return valoresPagosCooperativosTrabalho;
    }

    public void setValoresPagosCooperativosTrabalho(String valoresPagosCooperativosTrabalho) {
        this.valoresPagosCooperativosTrabalho = valoresPagosCooperativosTrabalho;
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

    public String getRegistroTipo12() {
        StringBuilder texto = new StringBuilder("");

        //Ordenacão, é removido posteriormente
        texto.append(ordenacao);
        //Tipo de Registro - 2 Posições
        texto.append(tipoDeRegistro);
        //Tipo de Inscricao - Empresa - 1 posição
        texto.append(StringUtil.cortaOuCompletaDireita(tipoDeInscricao, 1, " "));
        //Inscrição da Empresa - 14 posições
        texto.append(StringUtil.cortaOuCompletaDireita(inscricaoDaEmpresa.replace(".", "").replace("/", ""), 14, " "));
        //Zeros - 36 posições
        texto.append(StringUtil.cortaOuCompletaDireita(zeros, 36, "0"));
        //Deducao 13 salario Licenca Maternidade - 15 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(deducao13SalarioLicencaMaternidade, 15, "0"));
        //Receita Evento Desportivo/Patrocínio - 15 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(receitaEventoDesportivoPatrocinio, 15, "0"));
        //Indicativo Origem da Receita - 1 Posição
        texto.append(StringUtil.cortaOuCompletaDireita(indicativoOrigemDaReceita, 1, " "));
        //Comercialização da Produção - Pessoa Física - 15 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(comercializacaoDaProducaoPF, 15, "0"));
        //Comercialização da Produção - Pessoa Jurídica - 15 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(comercializacaoDaProducaoPJ, 15, "0"));
        //Outras Informações Processo - 11 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(outrasInformacoesProcesso, 11, " "));
        //Outras Informações Processo - Ano - 4 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(outrasInformacoesProcessoAno, 4, "0"));
        //Outras Informações Vara/JCJ - 5 Posicoes
        texto.append(StringUtil.cortaOuCompletaDireita(outrasInformacoesVara, 5, " "));
        //Outras Informacoes Periodo Inicio - 6 posições
        texto.append(StringUtil.cortaOuCompletaDireita(outrasInformacoesPeriodoInicio, 6, " "));
        //Outras Informacoes Periodo Fim - 6 posições
        texto.append(StringUtil.cortaOuCompletaDireita(outrasInformacoesPeriodoFim, 6, " "));
        //Compensação - Valor Corrigido - 15 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(compensacaoValorCorrigido, 15, "0"));
        //Compensacao - Período Início - 6 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(compensacaoPeriodoInicio, 6, " "));
        //Compensacao - Período Final - 6 posições
        texto.append(StringUtil.cortaOuCompletaDireita(outrasInformacoesPeriodoFim, 6, " "));
        //Recolhimento de Competências anteriores = valor do inss sobre fp - 15 posicoes
        texto.append(StringUtil.cortaOuCompletaEsquerda(recolhimentoCompetenciasAnterioresValorINSSFP, 15, "0"));
        //Recolhimento de Competências anteriores - Outras Entidades sobre FP - 15 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(recolhimentoCompetenciasAnterioresOutrasFP, 15, "0"));
        //Recolhimento de Competências anteriores - Comercialização de Produção Valor INSS - 15 posicoes
        texto.append(StringUtil.cortaOuCompletaEsquerda(recolhimentoCompetenciasAnterioresComercializacaoProducaoINSS, 15, "0"));
        //Recolhimento de Competências anteriores - comercialização de produção - outras entidades - 15 posicoes
        texto.append(StringUtil.cortaOuCompletaEsquerda(recolhimentoCompetenciasAnterioresComercializacaoProducaoOutras, 15, "0"));
        //Recolhimento de Competências anteriores - receita de evento deportivo/patrocinio - valor inss - 15 posicoes
        texto.append(StringUtil.cortaOuCompletaEsquerda(recolhimentoCompetenciasAnterioresEventoDesportivoPatrocinioINSS, 15, "0"));
        //Parcelamento do FGTS Categorias 01,02,03,05 e 06 - 15 posicoes
        texto.append(StringUtil.cortaOuCompletaEsquerda(parcelamentoFGTSCategorias1, 15, "0"));
        //Parcelamento do FGTS Categorias 04 e 07 - 15 posicoes
        texto.append(StringUtil.cortaOuCompletaEsquerda(parcelamentoFGTSCategorias2, 15, "0"));
        //Parcelamento do FGTS - Valor Recolhido - 15 posicoes
        texto.append(StringUtil.cortaOuCompletaEsquerda(parcelamentoFGTSValorRecolhido, 15, "0"));
        //Valores Pagos à Cooperativas de Trabalho - servicos prestados - 15 posicoes
        texto.append(StringUtil.cortaOuCompletaEsquerda(valoresPagosCooperativosTrabalho, 15, "0"));
        //Implementação Futura - 45 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(implementacaoFutura, 45, "0"));
        //Brancos - 6 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(brancos, 6, " "));

        String s = StringUtil.removeCaracteresEspeciais(texto.toString());

        //Final de Linha - 1 Posição
        s += StringUtil.cortaOuCompletaEsquerda(finalDeLinha, 1, "*");
        s += "\r\n";
        return s;
    }
}
