/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.obn350;

import br.com.webpublico.enums.TipoMovimentoArquivoBancario;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FormataValoresUtil;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Edi
 */
public class RegistroObn350TipoQuatro implements Serializable {

    private TipoMovimentoArquivoBancario tipoMovimentoArquivoBancario;
    private String linhas;
    private String tipoRegistro;
    private String codigoAgencaiaBancariaUGEmitente;
    private String codigoUGEmitenteDasObs;
    private String codigoRelacaoOndeContasOB;
    private String numeroMovimento;
    private String dataReferenciaDaRelacao;
    private String brancos;
    private String codigoOperacao;
    private String numeroSequencialDaLista;
    private String valorLiquidaOB;
    private String tipoDeFatura;
    private String codigoBarraFatura;
    private String dataVencimento;
    private String valorNominal;
    private String valorDescontoAbatido;
    private String valorMoraJuros;
    private String codigoBarraConvenio;
    private String observacaoOb;
    private String numeroAutenticacao;
    private String previxoAgenciaParaDebitosOBConvenios;
    private String numeroContaDoConvenioParaOBConvenios;
    private String codigoRetornoOperacao;
    private String numeroSequencialArquivo;
    private BigDecimal valorFinalGuia;
    private HeaderObn350 headerObn350;

    public RegistroObn350TipoQuatro() {
        throw new ExcecaoNegocioGenerica("Construtor não suportado, utilize o construtor informando uma linha \"Texto\" como paramentro!");
    }

    public RegistroObn350TipoQuatro(String linhas, HeaderObn350 headerObn350) {
        this.linhas = linhas;
        this.headerObn350 = headerObn350;
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
            throw new ExcecaoNegocioGenerica(" A linhas não atende o formato exigido pelo header, deveriater 350 char e contem " + linha.length() + "!");
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

    public String getCodigoRelacaoOndeContasOB() {
        codigoRelacaoOndeContasOB = linhas.substring(17, 28);
        return codigoRelacaoOndeContasOB;
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

    public String getBrancosIntervalo48A51() {
        brancos = linhas.substring(47, 51);
        return brancos;
    }


    public String getCodigoOperacao() {
        codigoOperacao = linhas.substring(51, 53);
        return codigoOperacao;
    }

    public String getBrancosIntervalo54A54() {
        brancos = linhas.substring(53, 54);
        return brancos;
    }

    public String getNumeroSequencialDaLista() {
        numeroSequencialDaLista = linhas.substring(54, 60);
        return numeroSequencialDaLista;
    }

    public String getBrancosIntervalo61A63() {
        brancos = linhas.substring(60, 63);
        return brancos;
    }

    public String getValorLiquidoOrdemBacariaConvertido() {
        valorLiquidaOB = linhas.substring(63, 80);
        return FormataValoresUtil.valorConvertidoR$(valorLiquidaOB);
    }

    public String getValorLiquidoOrdemBacaria() {
        valorLiquidaOB = linhas.substring(63, 80);
        return valorLiquidaOB;
    }

    public String getBrancosIntervalo81A95() {
        brancos = linhas.substring(80, 95);
        return brancos;
    }

    public String getTipoDeFatura() {
        tipoDeFatura = linhas.substring(95, 96);
        return tipoDeFatura;
    }

    public String getCodigoBarras() {
        codigoBarraFatura = linhas.substring(96, 140);
        return codigoBarraFatura;
    }


    //   TIPO 1 - FATURA
    public String getCodigoBarraFatura() {
        codigoBarraFatura = linhas.substring(96, 140);
        return codigoBarraFatura;
    }

    public String getDataVencimento() {
        dataVencimento = linhas.substring(140, 148);
        return dataVencimento.substring(0, 2) + "/" + dataVencimento.substring(2, 4) + "/" + dataVencimento.substring(4, 8);
    }

    public String getValorNominalConvertido() {
        valorNominal = linhas.substring(148, 165);
        return FormataValoresUtil.valorConvertidoR$(valorNominal);
    }

    public String getValorDescontoAbatidoConvertido() {
        valorDescontoAbatido = linhas.substring(165, 182);
        return FormataValoresUtil.valorConvertidoR$(valorDescontoAbatido);
    }

    public String getValorMoraJurosConvertido() {
        valorMoraJuros = linhas.substring(182, 199);
        return FormataValoresUtil.valorConvertidoR$(valorMoraJuros);
    }


    public String getValorNominal() {
        valorNominal = linhas.substring(148, 165);
        return valorNominal;
    }

    public String getValorDescontoAbatido() {
        valorDescontoAbatido = linhas.substring(165, 182);
        return valorDescontoAbatido;
    }

    public String getValorMoraJuros() {
        valorMoraJuros = linhas.substring(182, 199);
        return valorMoraJuros;
    }

    public BigDecimal getValorFinalGuia() {
        BigDecimal valor = new BigDecimal(new Double(retornaValorComPonto(getValorNominal().isEmpty() ? getValorNominal() : "000")));
        BigDecimal juros = new BigDecimal(new Double(retornaValorComPonto(getValorMoraJuros().isEmpty() ? getValorMoraJuros() : "000")));
        BigDecimal desconto = new BigDecimal(new Double(retornaValorComPonto(getValorDescontoAbatido().isEmpty() ? getValorDescontoAbatido() : "000")));
        valorFinalGuia = (valor.add(juros)).subtract(desconto);
        System.out.println("VALOR...: " + valor);
        System.out.println("JUROS...: " + juros);
        System.out.println("DESC....: " + desconto);
        System.out.println("FINAL...: " + valorFinalGuia);
        return valorFinalGuia;
    }

    public void setValorFinalGuia(BigDecimal valorFinalGuia) {
        this.valorFinalGuia = valorFinalGuia;
    }

    private String retornaValorComPonto(String valor) {
        StringBuilder stringBuilder = new StringBuilder(valor);
        stringBuilder.insert(valor.length() - 2, '.');
        return stringBuilder.toString();
    }
//    ------------------------------------


    //    TIPO 2 - CONVENIOS
    public String getCodigoBarraConvenios() {
        codigoBarraConvenio = linhas.substring(96, 140);
        return codigoBarraConvenio;
    }

    public String getBrancosIntervaloConvenios() {
        brancos = linhas.substring(140, 199);
        return brancos;
    }
//    ------------------------------------

    public String getBrancosIntervalo199A263() {
        brancos = linhas.substring(199, 263);
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

    public TipoMovimentoArquivoBancario getTipoMovimentoArquivoBancario() {
        return tipoMovimentoArquivoBancario;
    }

    public String getPrefixoAgenciaParaDebitosOBConvenios() {
        previxoAgenciaParaDebitosOBConvenios = linhas.substring(319, 324);
        return previxoAgenciaParaDebitosOBConvenios;
    }

    public String getNumeroContaDoConvenioParaOBConvenios() {
        if (headerObn350.isArquivoCaixaEconomica()) {
            numeroContaDoConvenioParaOBConvenios = linhas.substring(324, 337);
        } else {
            numeroContaDoConvenioParaOBConvenios = linhas.substring(324, 334);
        }
        return numeroContaDoConvenioParaOBConvenios;
    }

    public String getBrancosIntervalo335A341() {
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


}
