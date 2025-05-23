/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.obn350;

import br.com.webpublico.enums.TipoMovimentoArquivoBancario;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FormataValoresUtil;

import java.io.Serializable;

/**
 * @author Edi
 */
public class RegistroObn350TipoCinco implements Serializable {

    private TipoMovimentoArquivoBancario tipoMovimentoArquivoBancario;
    private String linhas;
    private String tipoRegistro;
    private String codigoAgencaiaBancariaUGEmitente;
    private String codigoUGEmitenteDasObs;
    private String codigoRelacaoConstaOB;
    private String numeroMovimento;
    private String dataReferenciaDaRelacao;
    private String brancos;
    private String codigoOperacao;
    private String numeroSequencialDaLista;
    private String valorPagamento;
    private String dataPagamento;
    private String tipoPagamento;

    //GUIA GPS
    private String codigoReceitaTributoGps;
    private String codigoIdentificadorTributoGps;
    private String mesCompetencia;
    private String valorPrevistoPagamentoINSS;
    private String valorOutrasEntidades;
    private String atualizacaoMonetaria;

    //GUIA DARF
    private String codigoReceitaTributoDarf;
    private String codigoIdentificadorTributoDarf;
    private String periodoApuracaoDarf;
    private String numeroReferencia;
    private String valorPrincipalDarf;
    private String valorMultaDarf;
    private String valorJurosEncargosDarf;
    private String dataVencimentoDarf;

    //GUIA DARF SIMPLES
    private String codigoReceitaTributoDarfSimples;
    private String codigoIdentificadorTributoDarfSimples;
    private String periodoApuracaoDarfSimples;
    private String valorReceitaBrutaAcumulada;
    private String percentualReceitaBrutaAcumulada;
    private String valorPrincipalDarfSimples;
    private String valorMultaDarfSimples;
    private String valorJurosEncargosDarfSimples;

    private String tipoIdentificaoContribuinte;
    private String codigoIdentificaoContribuinte;
    private String nomeContribuinte;
    private String observacaoOb;
    private String numeroAutenticacao;
    private String previxoAgenciaParaDebitosOBConvenios;
    private String numeroContaDoConvenioParaOBConvenios;
    private String codigoRetornoOperacao;
    private String numeroSequencialArquivo;

    public RegistroObn350TipoCinco() {
        throw new ExcecaoNegocioGenerica("Construtor não suportado, utilize o construtor informando uma linha \"Texto\" como paramentro!");
    }

    public RegistroObn350TipoCinco(String linhas) {
        this.linhas = linhas;
        validaLinha(this.linhas);
    }

    private void validaLinha(String linha) {
        if (linha == null) {
            throw new ExcecaoNegocioGenerica("O valor da linha para o header esta nula!");
        }
        if (linha.equals("")) {
            throw new ExcecaoNegocioGenerica("A linha para o header esta em Branco!");
        }
        if (linha.length() != 350) {
            throw new ExcecaoNegocioGenerica(" A linhas não atende o formato exigido pelo header, deveriater 350 char e contém " + linha.length() + "!");
        }
    }

    public String getTipoRegistro() {
        tipoRegistro = linhas.substring(0, 1);
        return tipoRegistro;
    }

    public String getCodigoAgencaiaBancariaUGEmitente() {
        codigoAgencaiaBancariaUGEmitente = linhas.substring(1, 6);
        return codigoAgencaiaBancariaUGEmitente.substring(0, 4) + "-" + codigoAgencaiaBancariaUGEmitente.substring(4, 5);
    }

    public String getCodigoUGEmitenteDasObs() {
        codigoUGEmitenteDasObs = linhas.substring(6, 17);
        return codigoUGEmitenteDasObs;
    }

    public String getCodigoRelacaoConstaOB() {
        codigoRelacaoConstaOB = linhas.substring(17, 28);
        return codigoRelacaoConstaOB;
    }

    public String getNumeroLancamento() {
        return getNumeroMovimento().substring(2, getNumeroMovimento().length()).trim();
    }

    public String getNumeroMovimento() {
        numeroMovimento = linhas.substring(28, 39);
        if (numeroMovimento.startsWith(TipoMovimentoArquivoBancario.PAGAMENTO.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.PAGAMENTO;
        } else if (numeroMovimento.startsWith(TipoMovimentoArquivoBancario.PAGAMENTO_EXTRA.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.PAGAMENTO_EXTRA;
        } else if (numeroMovimento.startsWith(TipoMovimentoArquivoBancario.TRANSFERENCIA_FINANCEIRA.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.TRANSFERENCIA_FINANCEIRA;
        } else if (numeroMovimento.startsWith(TipoMovimentoArquivoBancario.TRANSFERENCIA_MESMA_UNIDADE.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.TRANSFERENCIA_MESMA_UNIDADE;
        } else if (numeroMovimento.startsWith(TipoMovimentoArquivoBancario.LIBERACAO.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.LIBERACAO;
        }
        return numeroMovimento;
    }

    public String getDataReferenciaDaRelacao() {
        dataReferenciaDaRelacao = linhas.substring(39, 47);
        return dataReferenciaDaRelacao.substring(0, 2) + "/" + dataReferenciaDaRelacao.substring(2, 4) + "/" + dataReferenciaDaRelacao.substring(4, 8);
    }

    public String getBrancosIntervalo47A51() {
        brancos = linhas.substring(47, 51);
        return brancos;
    }

    public String getCodigoOperacao() {
        codigoOperacao = linhas.substring(51, 53);
        return codigoOperacao;
    }

    public String getBrancosIntervalo53A54() {
        brancos = linhas.substring(53, 54);
        return brancos;
    }

    public String getNumeroSequencialDaLista() {
        numeroSequencialDaLista = linhas.substring(54, 60);
        return numeroSequencialDaLista;
    }

    public String getBrancosIntervalo60A63() {
        brancos = linhas.substring(60, 63);
        return brancos;
    }

    public String getValorPagamento() {
        valorPagamento = linhas.substring(63, 80);
        return FormataValoresUtil.valorConvertidoR$(valorPagamento);
    }

    public String getValorFinalGuia() {
        return linhas.substring(63, 80);
    }

    public String getDataPagamento() {
        dataPagamento = linhas.substring(80, 88);
        return dataPagamento.substring(0, 2) + "/" + dataPagamento.substring(2, 4) + "/" + dataPagamento.substring(4, 8);
    }

    public String getBrancosIntervalo88A95() {
        brancos = linhas.substring(88, 95);
        return brancos;
    }

    public String getTipoPagamento() {
        tipoPagamento = linhas.substring(95, 96);
        return tipoPagamento;
    }

    public String getCodigoReceitaTributoGeral() {
        return linhas.substring(96, 102);
    }


    //   TIPO 1 - GPS
    public String getCodigoReceitaTributoGps() {
        codigoReceitaTributoGps = linhas.substring(96, 102);
        return codigoReceitaTributoGps;
    }

    public String getCodigoIdentificadorTributoGps() {
        codigoIdentificadorTributoGps = linhas.substring(102, 104);
        return codigoIdentificadorTributoGps;
    }

    public String getMesAnoCompetencia() {
        mesCompetencia = linhas.substring(104, 110);
        return mesCompetencia.substring(0, 2) + "/" + mesCompetencia.substring(2, 6);
    }

    public String getValorPrevistoPagamentoINSS() {
        valorPrevistoPagamentoINSS = linhas.substring(110, 127);
        return FormataValoresUtil.valorConvertidoR$(valorPrevistoPagamentoINSS);
    }

    public String getValorOutrasEntidades() {
        valorOutrasEntidades = linhas.substring(127, 144);
        return FormataValoresUtil.valorConvertidoR$(valorOutrasEntidades);
    }

    public String getAtualizacaoMonetaria() {
        atualizacaoMonetaria = linhas.substring(144, 161);
        return FormataValoresUtil.valorConvertidoR$(atualizacaoMonetaria);
    }

    public String getBrancosIntervalo160A188GPS() {
        brancos = linhas.substring(161, 188);
        return brancos;
    }
//    ------------------------------------


    //   TIPO 2 - DARF
    public String getCodigoReceitaTributoDarf() {
        codigoReceitaTributoDarf = linhas.substring(96, 102);
        return codigoReceitaTributoDarf;
    }

    public String getCodigoIdentificadorTributoDarf() {
        codigoIdentificadorTributoDarf = linhas.substring(102, 104);
        return codigoIdentificadorTributoDarf;
    }

    public String getPeriodoApuracaoDarf() {
        periodoApuracaoDarf = linhas.substring(104, 112);
        return periodoApuracaoDarf.substring(0, 2) + "/" + periodoApuracaoDarf.substring(3, 4) + "/" + periodoApuracaoDarf.substring(5, 8);
    }

    public String getNumeroReferencia() {
        numeroReferencia = linhas.substring(112, 129);
        return numeroReferencia;
    }

    public String getValorPrincipalDarf() {
        valorPrincipalDarf = linhas.substring(129, 146);
        return FormataValoresUtil.valorConvertidoR$(valorPrincipalDarf);
    }

    public String getValorMultaDarf() {
        valorMultaDarf = linhas.substring(146, 163);
        return FormataValoresUtil.valorConvertidoR$(valorMultaDarf);
    }

    public String getValorJurosEncargosDarf() {
        valorJurosEncargosDarf = linhas.substring(163, 180);
        return FormataValoresUtil.valorConvertidoR$(valorJurosEncargosDarf);
    }

    public String getDataVencimentoDarf() {
        dataVencimentoDarf = linhas.substring(180, 188);
        return dataVencimentoDarf.substring(0, 2) + "/" + dataVencimentoDarf.substring(2, 4) + "/" + dataVencimentoDarf.substring(5, 8);
    }
//    ------------------------------------


    //   TIPO 3 - DARF SIMPLES
    public String getCodigoReceitaTributoDarfSimples() {
        codigoReceitaTributoDarfSimples = linhas.substring(96, 102);
        return codigoReceitaTributoDarfSimples;
    }

    public String getCodigoIdentificadorTributoDarfSimples() {
        codigoIdentificadorTributoDarfSimples = linhas.substring(102, 104);
        return codigoIdentificadorTributoDarfSimples;
    }

    public String getPeriodoApuracaoDarfSimples() {
        periodoApuracaoDarfSimples = linhas.substring(104, 112);
        return periodoApuracaoDarfSimples.substring(0, 2) + "/" + periodoApuracaoDarfSimples.substring(2, 4) + "/" + periodoApuracaoDarfSimples.substring(5, 8);
    }

    public String getValorReceitaBrutaAcumulada() {
        valorReceitaBrutaAcumulada = linhas.substring(112, 129);
        return FormataValoresUtil.valorConvertidoR$(valorReceitaBrutaAcumulada);
    }

    public String getPercentualReceitaBrutaAcumulada() {
        percentualReceitaBrutaAcumulada = linhas.substring(129, 136);
        return percentualReceitaBrutaAcumulada;
    }

    public String getValorPrincipalDarfSimples() {
        valorPrincipalDarfSimples = linhas.substring(136, 153);
        return FormataValoresUtil.valorConvertidoR$(valorPrincipalDarfSimples);
    }

    public String getValorMultaDarfSimples() {
        valorMultaDarfSimples = linhas.substring(153, 170);
        return FormataValoresUtil.valorConvertidoR$(valorMultaDarfSimples);
    }

    public String getValorJurosEncargosDarfSimples() {
        valorJurosEncargosDarfSimples = linhas.substring(170, 187);
        return FormataValoresUtil.valorConvertidoR$(valorJurosEncargosDarfSimples);
    }

    public String getBrancosIntervalo187A188() {
        brancos = linhas.substring(187, 188);
        return brancos;
    }
//    ------------------------------------

    public String getTipoIdentificaoContribuinte() {
        tipoIdentificaoContribuinte = linhas.substring(187, 190);
        return tipoIdentificaoContribuinte;
    }

    public String getCodigoIdentificaoContribuinte() {
        codigoIdentificaoContribuinte = linhas.substring(190, 204);
        return codigoIdentificaoContribuinte;
    }

    public String getNomeContribuinte() {
        nomeContribuinte = linhas.substring(204, 234);
        return nomeContribuinte;
    }

    public String getBrancosIntervalo233A263() {
        brancos = linhas.substring(234, 263);
        return brancos;
    }

    public String getObservacaoOb() {
        observacaoOb = linhas.substring(263, 303);
        return observacaoOb;
    }

    public String getNumeroAutenticacao() {
        numeroAutenticacao = linhas.substring(303, 319);
        return numeroAutenticacao;
    }

    public String getPrefixoAgenciaParaDebitosOBConvenios() {
        previxoAgenciaParaDebitosOBConvenios = linhas.substring(319, 324);
        return previxoAgenciaParaDebitosOBConvenios;
    }

    public String getNumeroContaDoConvenioParaOBConvenios() {
        numeroContaDoConvenioParaOBConvenios = linhas.substring(324, 334);
        return numeroContaDoConvenioParaOBConvenios;
    }

    public String getBrancosIntervalo334A341() {
        brancos = linhas.substring(334, 341);
        return brancos;
    }

    public String getCodigoRetornoOperacao() {
        codigoRetornoOperacao = linhas.substring(341, 343);
        return codigoRetornoOperacao;
    }

    public boolean getObteveSucesso() {
        codigoRetornoOperacao = linhas.substring(341, 343);
        return codigoRetornoOperacao.equals("01");
    }

    public String getNumeroSequencialArquivo() {
        numeroSequencialArquivo = linhas.substring(343, 350);
        return numeroSequencialArquivo;
    }

//    public String getNumeroBordero() {
//        return getPrefixoAgenciaParaDebitosOBConvenios().trim().substring(2, this.getPrefixoAgenciaParaDebitosOBConvenios().length()).trim();
//    }

    public TipoMovimentoArquivoBancario getTipoMovimentoArquivoBancario() {
        return tipoMovimentoArquivoBancario;
    }
}
