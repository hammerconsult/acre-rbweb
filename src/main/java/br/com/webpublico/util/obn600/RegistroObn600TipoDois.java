/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.obn600;

import br.com.webpublico.enums.ConfiguracaoRegistroTipoDoisOBN600;
import br.com.webpublico.util.obn350.UtilArquivoBancario;
import com.google.common.base.Preconditions;

/**
 *
 * @author reidocrime
 */
public class RegistroObn600TipoDois {

    private String linhas;
    private String id2;
    private String codigoAgencaiaBancariaUndGestoraEmitente;
    private String codigoUnidadeGestoraEmitenteDasObs;
    private String numeroRelacaoExternaQueContasOrdemBancaria;
    private String numeroOrdemBancaria;
    private String dataReferenciaDaRelacao;
    private String brancos;
    private String codigoDaOperacao;
    private String indicadorTipoPagamentoPessoal;
    private String zeros;
    private String valorLiquidoOrdemBacaria;
    private String codigoBancoFavorecido;
    private String codigoAgenciaFavorecisaSemDigito;
    private String digitoVerificador;
    private String codigoContaCorrentaFavorecidoSemDigito;
    private String nomeFavorecido;
    private String enderecoFavorecido;
    private String municipioFavorecido;
    private String codigoCiaf_GRU;
    private String cepFavorecido;
    private String ufFavorecido;
    private String observacaoOb;
    private String indicadorTipoPagamento;
    private String tipoFavorecio;
    private String codigoFavorecido;
    private String prefixoDVagenciaParaDebito;
    private String numeroContaConvenioDV;
    private String finalidadePagamentoFUNDEB;
    private String branco;
    private String codigoDeRetornoOperacao;
    private String numeroSequencialNoArquivo;

    public RegistroObn600TipoDois() {
    }

    public RegistroObn600TipoDois(String id2, String codigoAgencaiaBancariaUndGestoraEmitente, String codigoUnidadeGestoraEmitenteDasObs, String numeroRelacaoExternaQueContasOrdemBancaria, String numeroOrdemBancaria, String dataReferenciaDaRelacao, String brancos, String codigoDaOperacao, String indicadorTipoPagamentoPessoal, String zeros, String valorLiquidoOrdemBacaria, String codigoBancoFavorecido, String codigoAgenciaFavorecisaSemDigito, String digitoVerificador, String codigoContaCorrentaFavorecidoSemDigito, String nomeFavorecido, String enderecoFavorecido, String municipioFavorecido, String codigoCiaf_GRU, String cepFavorecido, String ufFavorecido, String observacaoOb, String indicadorTipoPagamento, String tipoFavorecio, String codigoFavorecido, String prefixoDVagenciaParaDebito, String numeroContaConvenioDV, String finalidadePagamentoFUNDEB, String branco, String codigoDeRetornoOperacao, String numeroSequencialNoArquivo) {
        this.id2 = id2;
        this.codigoAgencaiaBancariaUndGestoraEmitente = codigoAgencaiaBancariaUndGestoraEmitente;
        this.codigoUnidadeGestoraEmitenteDasObs = codigoUnidadeGestoraEmitenteDasObs;
        this.numeroRelacaoExternaQueContasOrdemBancaria = numeroRelacaoExternaQueContasOrdemBancaria;
        this.numeroOrdemBancaria = numeroOrdemBancaria;
        this.dataReferenciaDaRelacao = dataReferenciaDaRelacao;
        this.brancos = brancos;
        this.codigoDaOperacao = codigoDaOperacao;
        this.indicadorTipoPagamentoPessoal = indicadorTipoPagamentoPessoal;
        this.zeros = zeros;
        this.valorLiquidoOrdemBacaria = valorLiquidoOrdemBacaria;
        this.codigoBancoFavorecido = codigoBancoFavorecido;
        this.codigoAgenciaFavorecisaSemDigito = codigoAgenciaFavorecisaSemDigito;
        this.digitoVerificador = digitoVerificador;
        this.codigoContaCorrentaFavorecidoSemDigito = codigoContaCorrentaFavorecidoSemDigito;
        this.nomeFavorecido = nomeFavorecido;
        this.enderecoFavorecido = enderecoFavorecido;
        this.municipioFavorecido = municipioFavorecido;
        this.codigoCiaf_GRU = codigoCiaf_GRU;
        this.cepFavorecido = cepFavorecido;
        this.ufFavorecido = ufFavorecido;
        this.observacaoOb = observacaoOb;
        this.indicadorTipoPagamento = indicadorTipoPagamento;
        this.tipoFavorecio = tipoFavorecio;
        this.codigoFavorecido = codigoFavorecido;
        this.prefixoDVagenciaParaDebito = prefixoDVagenciaParaDebito;
        this.numeroContaConvenioDV = numeroContaConvenioDV;
        this.finalidadePagamentoFUNDEB = finalidadePagamentoFUNDEB;
        this.branco = branco;
        this.codigoDeRetornoOperacao = codigoDeRetornoOperacao;
        this.numeroSequencialNoArquivo = numeroSequencialNoArquivo;
        validaNull();
        formataInformacoes();
        validaTamanhos();
        validaCaracteresErrados();
    }

    private void validaCaracteresErrados() {
        UtilArquivoBancario.soBrancos(branco);
        UtilArquivoBancario.soBrancos(brancos);
        UtilArquivoBancario.soBrancos(zeros);
    }

    private void validaTamanhos() {
        Preconditions.checkArgument(id2.length() == ConfiguracaoRegistroTipoDoisOBN600.ID2.getTamanho());
        Preconditions.checkArgument(codigoAgencaiaBancariaUndGestoraEmitente.length() == ConfiguracaoRegistroTipoDoisOBN600.CODIGOAGENCAIABANCARIAUNDGESTORAEMITENTE.getTamanho());
        Preconditions.checkArgument(codigoUnidadeGestoraEmitenteDasObs.length() == ConfiguracaoRegistroTipoDoisOBN600.CODIGOUNIDADEGESTORAEMITENTEDASOBS.getTamanho());
        Preconditions.checkArgument(numeroRelacaoExternaQueContasOrdemBancaria.length() == ConfiguracaoRegistroTipoDoisOBN600.NUMERORELACAOEXTERNAQUECONTASORDEMBANCARIA.getTamanho());
        Preconditions.checkArgument(numeroOrdemBancaria.length() == ConfiguracaoRegistroTipoDoisOBN600.NUMEROORDEMBANCARIA.getTamanho());
        Preconditions.checkArgument(dataReferenciaDaRelacao.length() == ConfiguracaoRegistroTipoDoisOBN600.DATAREFERENCIADARELACAO.getTamanho());
        Preconditions.checkArgument(brancos.length() == ConfiguracaoRegistroTipoDoisOBN600.BRANCOS.getTamanho());
        Preconditions.checkArgument(codigoDaOperacao.length() == ConfiguracaoRegistroTipoDoisOBN600.CODIGODAOPERACAO.getTamanho());
        Preconditions.checkArgument(indicadorTipoPagamentoPessoal.length() == ConfiguracaoRegistroTipoDoisOBN600.INDICADORTIPOPAGAMENTOPESSOAL.getTamanho());
        Preconditions.checkArgument(zeros.length() == ConfiguracaoRegistroTipoDoisOBN600.ZEROS.getTamanho());
        Preconditions.checkArgument(valorLiquidoOrdemBacaria.length() == ConfiguracaoRegistroTipoDoisOBN600.VALORLIQUIDOORDEMBACARIA.getTamanho());
        Preconditions.checkArgument(codigoBancoFavorecido.length() == ConfiguracaoRegistroTipoDoisOBN600.CODIGOBANCOFAVORECIDO.getTamanho());
        Preconditions.checkArgument(codigoAgenciaFavorecisaSemDigito.length() == ConfiguracaoRegistroTipoDoisOBN600.CODIGOAGENCIAFAVORECISASEMDIGITO.getTamanho());
        Preconditions.checkArgument(digitoVerificador.length() == ConfiguracaoRegistroTipoDoisOBN600.DIGITOVERIFICADOR.getTamanho());
        Preconditions.checkArgument(codigoContaCorrentaFavorecidoSemDigito.length() == ConfiguracaoRegistroTipoDoisOBN600.CODIGOCONTACORRENTAFAVORECIDOSEMDIGITO.getTamanho());
        Preconditions.checkArgument(nomeFavorecido.length() == ConfiguracaoRegistroTipoDoisOBN600.NOMEFAVORECIDO.getTamanho());
        Preconditions.checkArgument(enderecoFavorecido.length() == ConfiguracaoRegistroTipoDoisOBN600.ENDERECOFAVORECIDO.getTamanho());
        Preconditions.checkArgument(municipioFavorecido.length() == ConfiguracaoRegistroTipoDoisOBN600.MUNICIPIOFAVORECIDO.getTamanho());
        Preconditions.checkArgument(codigoCiaf_GRU.length() == ConfiguracaoRegistroTipoDoisOBN600.CODIGOCIAF_GRU.getTamanho());
        Preconditions.checkArgument(cepFavorecido.length() == ConfiguracaoRegistroTipoDoisOBN600.CEPFAVORECIDO.getTamanho());
        Preconditions.checkArgument(ufFavorecido.length() == ConfiguracaoRegistroTipoDoisOBN600.UFFAVORECIDO.getTamanho());
        Preconditions.checkArgument(observacaoOb.length() == ConfiguracaoRegistroTipoDoisOBN600.OBSERVACAOOB.getTamanho());
        Preconditions.checkArgument(indicadorTipoPagamento.length() == ConfiguracaoRegistroTipoDoisOBN600.INDICADORTIPOPAGAMENTO.getTamanho());
        Preconditions.checkArgument(tipoFavorecio.length() == ConfiguracaoRegistroTipoDoisOBN600.TIPOFAVORECIO.getTamanho());
        Preconditions.checkArgument(codigoFavorecido.length() == ConfiguracaoRegistroTipoDoisOBN600.CODIGOFAVORECIDO.getTamanho());
        Preconditions.checkArgument(prefixoDVagenciaParaDebito.length() == ConfiguracaoRegistroTipoDoisOBN600.PREFIXODVAGENCIAPARADEBITO.getTamanho());
        Preconditions.checkArgument(numeroContaConvenioDV.length() == ConfiguracaoRegistroTipoDoisOBN600.NUMEROCONTACONVENIODV.getTamanho());
        Preconditions.checkArgument(finalidadePagamentoFUNDEB.length() == ConfiguracaoRegistroTipoDoisOBN600.FINALIDADEPAGAMENTOFUNDEB.getTamanho());
        Preconditions.checkArgument(branco.length() == ConfiguracaoRegistroTipoDoisOBN600.BRANCO.getTamanho());
        Preconditions.checkArgument(codigoDeRetornoOperacao.length() == ConfiguracaoRegistroTipoDoisOBN600.CODIGODERETORNOOPERACAO.getTamanho());
        Preconditions.checkArgument(numeroSequencialNoArquivo.length() == ConfiguracaoRegistroTipoDoisOBN600.NUMEROSEQUENCIALNOARQUIVO.getTamanho());
    }

    private void formataInformacoes() {
        id2 = UtilArquivoBancario.completaZerosEsquera(id2, ConfiguracaoRegistroTipoDoisOBN600.ID2.getTamanho());
        codigoAgencaiaBancariaUndGestoraEmitente = UtilArquivoBancario.completaZerosEsquera(codigoAgencaiaBancariaUndGestoraEmitente, ConfiguracaoRegistroTipoDoisOBN600.UFFAVORECIDO.getTamanho());
        codigoUnidadeGestoraEmitenteDasObs = UtilArquivoBancario.completaZerosEsquera(codigoUnidadeGestoraEmitenteDasObs, ConfiguracaoRegistroTipoDoisOBN600.UFFAVORECIDO.getTamanho());
        //numeroRelacaoExternaQueContasOrdemBancaria = UtilArquivoBancario.completaZerosEsquera(numeroRelacaoExternaQueContasOrdemBancaria, ConfiguracaoRegistroTipoDoisOBN600.UFFAVORECIDO.getTamanho());
        numeroOrdemBancaria = UtilArquivoBancario.completaZerosEsquera(numeroOrdemBancaria, ConfiguracaoRegistroTipoDoisOBN600.NUMEROORDEMBANCARIA.getTamanho());
        dataReferenciaDaRelacao = UtilArquivoBancario.completaZerosEsquera(dataReferenciaDaRelacao, ConfiguracaoRegistroTipoDoisOBN600.DATAREFERENCIADARELACAO.getTamanho());
        brancos = UtilArquivoBancario.completaZerosEsquera(brancos, ConfiguracaoRegistroTipoDoisOBN600.BRANCOS.getTamanho());
        codigoDaOperacao = UtilArquivoBancario.completaZerosEsquera(codigoDaOperacao, ConfiguracaoRegistroTipoDoisOBN600.CODIGODAOPERACAO.getTamanho());
        indicadorTipoPagamentoPessoal = UtilArquivoBancario.completaZerosEsquera(indicadorTipoPagamentoPessoal, ConfiguracaoRegistroTipoDoisOBN600.INDICADORTIPOPAGAMENTOPESSOAL.getTamanho());
        zeros = UtilArquivoBancario.completaZerosEsquera(zeros, ConfiguracaoRegistroTipoDoisOBN600.ZEROS.getTamanho());
        valorLiquidoOrdemBacaria = UtilArquivoBancario.completaZerosEsquera(valorLiquidoOrdemBacaria, ConfiguracaoRegistroTipoDoisOBN600.VALORLIQUIDOORDEMBACARIA.getTamanho());
        codigoBancoFavorecido = UtilArquivoBancario.completaZerosEsquera(codigoBancoFavorecido, ConfiguracaoRegistroTipoDoisOBN600.CODIGOBANCOFAVORECIDO.getTamanho());
        codigoAgenciaFavorecisaSemDigito = UtilArquivoBancario.completaZerosEsquera(codigoAgenciaFavorecisaSemDigito, ConfiguracaoRegistroTipoDoisOBN600.CODIGOAGENCIAFAVORECISASEMDIGITO.getTamanho());
        digitoVerificador = UtilArquivoBancario.completaZerosEsquera(digitoVerificador, ConfiguracaoRegistroTipoDoisOBN600.DIGITOVERIFICADOR.getTamanho());
        codigoContaCorrentaFavorecidoSemDigito = UtilArquivoBancario.completaZerosEsquera(codigoContaCorrentaFavorecidoSemDigito, ConfiguracaoRegistroTipoDoisOBN600.CODIGOCONTACORRENTAFAVORECIDOSEMDIGITO.getTamanho());
        nomeFavorecido = UtilArquivoBancario.completaBrancoEsquera(nomeFavorecido, ConfiguracaoRegistroTipoDoisOBN600.NOMEFAVORECIDO.getTamanho());
        enderecoFavorecido = UtilArquivoBancario.completaBrancoEsquera(enderecoFavorecido, ConfiguracaoRegistroTipoDoisOBN600.ENDERECOFAVORECIDO.getTamanho());
        municipioFavorecido = UtilArquivoBancario.completaBrancoEsquera(municipioFavorecido, ConfiguracaoRegistroTipoDoisOBN600.MUNICIPIOFAVORECIDO.getTamanho());
        codigoCiaf_GRU = UtilArquivoBancario.completaZerosEsquera(codigoCiaf_GRU, ConfiguracaoRegistroTipoDoisOBN600.CODIGOCIAF_GRU.getTamanho());
        cepFavorecido = UtilArquivoBancario.completaBrancoEsquera(cepFavorecido, ConfiguracaoRegistroTipoDoisOBN600.CEPFAVORECIDO.getTamanho());
        ufFavorecido = UtilArquivoBancario.completaBrancoEsquera(ufFavorecido, ConfiguracaoRegistroTipoDoisOBN600.UFFAVORECIDO.getTamanho());
        observacaoOb = UtilArquivoBancario.completaBrancoEsquera(observacaoOb, ConfiguracaoRegistroTipoDoisOBN600.OBSERVACAOOB.getTamanho());
        indicadorTipoPagamento = UtilArquivoBancario.completaZerosEsquera(indicadorTipoPagamento, ConfiguracaoRegistroTipoDoisOBN600.INDICADORTIPOPAGAMENTO.getTamanho());
        tipoFavorecio = UtilArquivoBancario.completaZerosEsquera(tipoFavorecio, ConfiguracaoRegistroTipoDoisOBN600.TIPOFAVORECIO.getTamanho());
        codigoFavorecido = UtilArquivoBancario.completaZerosEsquera(codigoFavorecido, ConfiguracaoRegistroTipoDoisOBN600.CODIGOFAVORECIDO.getTamanho());
        prefixoDVagenciaParaDebito = UtilArquivoBancario.completaBrancoDireita(prefixoDVagenciaParaDebito, ConfiguracaoRegistroTipoDoisOBN600.PREFIXODVAGENCIAPARADEBITO.getTamanho());
        numeroContaConvenioDV = UtilArquivoBancario.completaBrancoDireita(numeroContaConvenioDV, ConfiguracaoRegistroTipoDoisOBN600.NUMEROCONTACONVENIODV.getTamanho());
        finalidadePagamentoFUNDEB = UtilArquivoBancario.completaBrancoDireita(finalidadePagamentoFUNDEB, ConfiguracaoRegistroTipoDoisOBN600.FINALIDADEPAGAMENTOFUNDEB.getTamanho());
        branco = UtilArquivoBancario.completaBrancoDireita(branco, ConfiguracaoRegistroTipoDoisOBN600.BRANCO.getTamanho());
        codigoDeRetornoOperacao = UtilArquivoBancario.completaZerosEsquera(codigoDeRetornoOperacao, ConfiguracaoRegistroTipoDoisOBN600.CODIGODERETORNOOPERACAO.getTamanho());
        numeroSequencialNoArquivo = UtilArquivoBancario.completaZerosEsquera(numeroSequencialNoArquivo, ConfiguracaoRegistroTipoDoisOBN600.NUMEROSEQUENCIALNOARQUIVO.getTamanho());

    }

    private void validaNull() {
        Preconditions.checkNotNull(id2);
        Preconditions.checkNotNull(codigoAgencaiaBancariaUndGestoraEmitente);
        Preconditions.checkNotNull(codigoUnidadeGestoraEmitenteDasObs);
        Preconditions.checkNotNull(numeroRelacaoExternaQueContasOrdemBancaria);
        Preconditions.checkNotNull(numeroOrdemBancaria);
        Preconditions.checkNotNull(dataReferenciaDaRelacao);
        Preconditions.checkNotNull(brancos);
        Preconditions.checkNotNull(codigoDaOperacao);
        Preconditions.checkNotNull(indicadorTipoPagamentoPessoal);
        Preconditions.checkNotNull(zeros);
        Preconditions.checkNotNull(valorLiquidoOrdemBacaria);
        Preconditions.checkNotNull(codigoBancoFavorecido);
        Preconditions.checkNotNull(codigoAgenciaFavorecisaSemDigito);
        Preconditions.checkNotNull(digitoVerificador);
        Preconditions.checkNotNull(codigoContaCorrentaFavorecidoSemDigito);
        Preconditions.checkNotNull(nomeFavorecido);
        Preconditions.checkNotNull(enderecoFavorecido);
        Preconditions.checkNotNull(municipioFavorecido);
        Preconditions.checkNotNull(codigoCiaf_GRU);
        Preconditions.checkNotNull(cepFavorecido);
        Preconditions.checkNotNull(ufFavorecido);
        Preconditions.checkNotNull(observacaoOb);
        Preconditions.checkNotNull(indicadorTipoPagamento);
        Preconditions.checkNotNull(tipoFavorecio);
        Preconditions.checkNotNull(codigoFavorecido);
        Preconditions.checkNotNull(prefixoDVagenciaParaDebito);
        Preconditions.checkNotNull(numeroContaConvenioDV);
        Preconditions.checkNotNull(finalidadePagamentoFUNDEB);
        Preconditions.checkNotNull(branco);
        Preconditions.checkNotNull(codigoDeRetornoOperacao);
        Preconditions.checkNotNull(numeroSequencialNoArquivo);

    }

    public String getLinhas() {
        return linhas;
    }

    public String getId2() {
        return id2;
    }

    public String getCodigoAgencaiaBancariaUndGestoraEmitente() {
        return codigoAgencaiaBancariaUndGestoraEmitente;
    }

    public String getCodigoUnidadeGestoraEmitenteDasObs() {
        return codigoUnidadeGestoraEmitenteDasObs;
    }

    public String getNumeroRelacaoExternaQueContasOrdemBancaria() {
        return numeroRelacaoExternaQueContasOrdemBancaria;
    }

    public String getNumeroOrdemBancaria() {
        return numeroOrdemBancaria;
    }

    public String getDataReferenciaDaRelacao() {
        return dataReferenciaDaRelacao;
    }

    public String getBrancos() {
        return brancos;
    }

    public String getCodigoDaOperacao() {
        return codigoDaOperacao;
    }

    public String getIndicadorTipoPagamentoPessoal() {
        return indicadorTipoPagamentoPessoal;
    }

    public String getZeros() {
        return zeros;
    }

    public String getValorLiquidoOrdemBacaria() {
        return valorLiquidoOrdemBacaria;
    }

    public String getCodigoBancoFavorecido() {
        return codigoBancoFavorecido;
    }

    public String getCodigoAgenciaFavorecisaSemDigito() {
        return codigoAgenciaFavorecisaSemDigito;
    }

    public String getDigitoVerificador() {
        return digitoVerificador;
    }

    public String getCodigoContaCorrentaFavorecidoSemDigito() {
        return codigoContaCorrentaFavorecidoSemDigito;
    }

    public String getNomeFavorecido() {
        return nomeFavorecido;
    }

    public String getEnderecoFavorecido() {
        return enderecoFavorecido;
    }

    public String getMunicipioFavorecido() {
        return municipioFavorecido;
    }

    public String getCodigoCiaf_GRU() {
        return codigoCiaf_GRU;
    }

    public String getCepFavorecido() {
        return cepFavorecido;
    }

    public String getUfFavorecido() {
        return ufFavorecido;
    }

    public String getObservacaoOb() {
        return observacaoOb;
    }

    public String getIndicadorTipoPagamento() {
        return indicadorTipoPagamento;
    }

    public String getTipoFavorecio() {
        return tipoFavorecio;
    }

    public String getCodigoFavorecido() {
        return codigoFavorecido;
    }

    public String getPrefixoDVagenciaParaDebito() {
        return prefixoDVagenciaParaDebito;
    }

    public String getNumeroContaConvenioDV() {
        return numeroContaConvenioDV;
    }

    public String getFinalidadePagamentoFUNDEB() {
        return finalidadePagamentoFUNDEB;
    }

    public String getBranco() {
        return branco;
    }

    public String getCodigoDeRetornoOperacao() {
        return codigoDeRetornoOperacao;
    }

    public String getNumeroSequencialNoArquivo() {
        return numeroSequencialNoArquivo;
    }
}
