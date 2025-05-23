/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.obn350;

import br.com.webpublico.enums.TipoMovimentoArquivoBancario;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FormataValoresUtil;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * @author reidocrime
 */
public class RegistroObn350TipoDois implements Serializable {

    private String linhas;
    private String id2;
    private String codigoAgencaiaBancariaUndGestoraEmitente;
    private String codigoUnidadeGestoraEmitenteDasObs;
    private String numeroRelacaoExternaQueContasOrdemBancaria;
    private String numeroOrdemBancaria;
    private TipoMovimentoArquivoBancario tipoMovimentoArquivoBancario;
    private String dataReferenciaDaRelacao;
    private String branco;
    private String codigoOperacao;
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
    private String brancos;
    private String codigoDeRetornoOperacao;
    private String numeroSequencialNoArquivo;
    private List<RegistroObn350TipoTres> registrosObn350TipoTres;
    private List<RegistroObn350TipoQuatro> registroObn350TipoQuatros;
    private List<RegistroObn350TipoCinco> registroObn350TipoCincos;
    private HashMap<String, Boolean> mapObtiveramSucesso;

    public RegistroObn350TipoDois() {
        throw new ExcecaoNegocioGenerica("Construtor não suportado, utilize o construtor informando uma linha \"Texto\" como paramentro!");
    }

    public RegistroObn350TipoDois(String linhas) {
        registrosObn350TipoTres = Lists.newArrayList();
        registroObn350TipoQuatros = Lists.newArrayList();
        registroObn350TipoCincos = Lists.newArrayList();
        mapObtiveramSucesso = new HashMap<>();
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
            throw new ExcecaoNegocioGenerica(" A linhas não atende o formato exigido pelo header, deveriater 350 char e contem " + linha.length() + "!");
        }

    }

    public String getLinhas() {
        return linhas;
    }

    public String getId2() {
        id2 = linhas.substring(0, 1);
        return id2;
    }

    public String getCodigoAgencaiaBancariaUndGestoraEmitente() {
        codigoAgencaiaBancariaUndGestoraEmitente = linhas.substring(1, 6);
        return codigoAgencaiaBancariaUndGestoraEmitente;
    }

    public String getCodigoUnidadeGestoraEmitenteDasObs() {
        codigoUnidadeGestoraEmitenteDasObs = linhas.substring(6, 17);
        return codigoUnidadeGestoraEmitenteDasObs;
    }

    public String getNumeroRelacaoExternaQueContasOrdemBancaria() {
        numeroRelacaoExternaQueContasOrdemBancaria = linhas.substring(17, 28);
        return numeroRelacaoExternaQueContasOrdemBancaria;
    }

    public String getNumeroLancamento() {
        return getNumeroMovimento().substring(2, getNumeroMovimento().length()).trim();
    }

    public String getNumeroMovimento() {
        numeroOrdemBancaria = linhas.substring(28, 39);
        if (numeroOrdemBancaria.startsWith(TipoMovimentoArquivoBancario.PAGAMENTO.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.PAGAMENTO;
        } else if (numeroOrdemBancaria.startsWith(TipoMovimentoArquivoBancario.PAGAMENTO_EXTRA.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.PAGAMENTO_EXTRA;
        } else if (numeroOrdemBancaria.startsWith(TipoMovimentoArquivoBancario.TRANSFERENCIA_FINANCEIRA.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.TRANSFERENCIA_FINANCEIRA;
        } else if (numeroOrdemBancaria.startsWith(TipoMovimentoArquivoBancario.TRANSFERENCIA_MESMA_UNIDADE.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.TRANSFERENCIA_MESMA_UNIDADE;
        } else if (numeroOrdemBancaria.startsWith(TipoMovimentoArquivoBancario.LIBERACAO.getCodigo())) {
            tipoMovimentoArquivoBancario = TipoMovimentoArquivoBancario.LIBERACAO;
        }
        return numeroOrdemBancaria;
    }

    public String getDataReferenciaDaRelacao() {
        dataReferenciaDaRelacao = linhas.substring(39, 47);
        return dataReferenciaDaRelacao.substring(0, 2) + "/" + dataReferenciaDaRelacao.substring(2, 4) + "/" + dataReferenciaDaRelacao.substring(4, 8);
    }

    public String getBranco() {
        branco = linhas.substring(47, 51);
        return branco;
    }

    public String getCodigoOperacao() {
        codigoOperacao = linhas.substring(51, 53);
        return codigoOperacao;
    }

    public String getZeros() {
        zeros = linhas.substring(53, 63);
        return zeros;
    }

    public String getValorLiquidoOrdemBacaria() {

        valorLiquidoOrdemBacaria = linhas.substring(63, 80);
        int tam = valorLiquidoOrdemBacaria.length();
        return valorLiquidoOrdemBacaria.substring(0, tam - 2) + "," + valorLiquidoOrdemBacaria.substring(tam - 2, tam);
    }

    public String getValorLiquidoOrdemBacariaConvertido() {
        valorLiquidoOrdemBacaria = linhas.substring(63, 80);
//        Double valor = Double.valueOf(valorLiquidoOrdemBacaria);
//        BigDecimal retorno = BigDecimal.valueOf(valor);
//        return Util.formataValor(retorno);

        return FormataValoresUtil.valorConvertidoR$(valorLiquidoOrdemBacaria);
    }

//    Não remover, usado no relatório do arquivo OBN350
    public BigDecimal getValorLiquidoOrdemBacariaParaRelatorio() {
        valorLiquidoOrdemBacaria = linhas.substring(63, 80);
        StringBuilder stringBuilder = new StringBuilder(valorLiquidoOrdemBacaria);
        stringBuilder.insert(valorLiquidoOrdemBacaria.length() - 2, '.');
        return new BigDecimal(new Double(stringBuilder.toString()));
    }

    public String getCodigoBancoFavorecido() {
        codigoBancoFavorecido = linhas.substring(80, 83);
        return codigoBancoFavorecido;
    }

    public String getCodigoAgenciaFavorecisaSemDigito() {
        codigoAgenciaFavorecisaSemDigito = linhas.substring(83, 87);
        return codigoAgenciaFavorecisaSemDigito;
    }

    public String getDigitoVerificador() {
        digitoVerificador = linhas.substring(87, 88);
        return digitoVerificador;
    }

    public String getCodigoContaCorrentaFavorecidoSemDigito() {
        codigoContaCorrentaFavorecidoSemDigito = linhas.substring(88, 98);
        return codigoContaCorrentaFavorecidoSemDigito;
    }

    public String getNomeFavorecido() {
        nomeFavorecido = linhas.substring(98, 143);
        return nomeFavorecido;
    }

    public String getEnderecoFavorecido() {
        enderecoFavorecido = linhas.substring(143, 208);
        return enderecoFavorecido;
    }

    public String getMunicipioFavorecido() {
        municipioFavorecido = linhas.substring(208, 236);
        return municipioFavorecido;
    }

    public String getCodigoCiaf_GRU() {
        codigoCiaf_GRU = linhas.substring(236, 253);
        return codigoCiaf_GRU;
    }

    public String getCepFavorecido() {
        cepFavorecido = linhas.substring(253, 261);
        return cepFavorecido;
    }

    public String getUfFavorecido() {
        ufFavorecido = linhas.substring(261, 263);
        return ufFavorecido;
    }

    public String getObservacaoOb() {
        observacaoOb = linhas.substring(263, 303);
        return observacaoOb;
    }

    public String getIndicadorTipoPagamento() {
        indicadorTipoPagamento = linhas.substring(303, 304);
        return indicadorTipoPagamento;
    }

    public String getTipoFavorecio() {
        tipoFavorecio = linhas.substring(304, 305);
        return tipoFavorecio;
    }

    public String getCodigoFavorecido() {
        codigoFavorecido = linhas.substring(305, 319);
        return codigoFavorecido;
    }

    public String getPrefixoDVagenciaParaDebito() {
        prefixoDVagenciaParaDebito = linhas.substring(319, 324);
        return prefixoDVagenciaParaDebito;
    }

    public String getNumeroContaConvenioDV() {
        numeroContaConvenioDV = linhas.substring(324, 334);
        return numeroContaConvenioDV;
    }

    public String getFinalidadePagamentoFUNDEB() {
        finalidadePagamentoFUNDEB = linhas.substring(334, 337);
        return finalidadePagamentoFUNDEB;
    }

    public String getBrancos() {
        brancos = linhas.substring(337, 341);
        return brancos;
    }

    public String getCodigoDeRetornoOperacao() {
        codigoDeRetornoOperacao = linhas.substring(341, 343);
        return codigoDeRetornoOperacao;
    }

    public String getNumeroSequencialNoArquivo() {
        numeroSequencialNoArquivo = linhas.substring(343, 350);
        return numeroSequencialNoArquivo;
    }

    public boolean getObteveSucesso() {
        String numeroMovimento = getNumeroMovimento();

        if (!mapObtiveramSucesso.containsKey(numeroMovimento)) {
            if (TipoMovimentoArquivoBancario.PAGAMENTO.equals(tipoMovimentoArquivoBancario) ||
                TipoMovimentoArquivoBancario.PAGAMENTO_EXTRA.equals(tipoMovimentoArquivoBancario)) {
                for (RegistroObn350TipoTres tipoTres : registrosObn350TipoTres) {
                    if (tipoTres.getNumeroMovimento().equals(numeroMovimento) && !tipoTres.getObteveSucesso()) {
                        mapObtiveramSucesso.put(numeroMovimento, false);
                        return false;
                    }
                }

                for (RegistroObn350TipoQuatro tipoQuatro : registroObn350TipoQuatros) {
                    if (tipoQuatro.getNumeroMovimento().equals(numeroMovimento) && !tipoQuatro.getObteveSucesso()) {
                        mapObtiveramSucesso.put(numeroMovimento, false);
                        return false;
                    }
                }

                for (RegistroObn350TipoCinco tipoCinco : registroObn350TipoCincos) {
                    if (tipoCinco.getNumeroMovimento().equals(numeroMovimento) && !tipoCinco.getObteveSucesso()) {
                        mapObtiveramSucesso.put(numeroMovimento, false);
                        return false;
                    }
                }
            }

            codigoDeRetornoOperacao = linhas.substring(341, 343);
            mapObtiveramSucesso.put(numeroMovimento, "01".equals(codigoDeRetornoOperacao));
        }

        return mapObtiveramSucesso.get(numeroMovimento);
    }

    public TipoMovimentoArquivoBancario getTipoMovimentoArquivoBancario() {
        return tipoMovimentoArquivoBancario;
    }

    public String getNumeroBordero() {
        return getNumeroRelacaoExternaQueContasOrdemBancaria().trim().substring(2, this.getNumeroRelacaoExternaQueContasOrdemBancaria().length()).trim();
    }

    public List<RegistroObn350TipoCinco> getRegistroObn350TipoCincos() {
        return registroObn350TipoCincos;
    }

    public void setRegistroObn350TipoCincos(List<RegistroObn350TipoCinco> registroObn350TipoCincos) {
        this.registroObn350TipoCincos = registroObn350TipoCincos;
    }

    public List<RegistroObn350TipoQuatro> getRegistroObn350TipoQuatros() {
        return registroObn350TipoQuatros;
    }

    public void setRegistroObn350TipoQuatros(List<RegistroObn350TipoQuatro> registroObn350TipoQuatros) {
        this.registroObn350TipoQuatros = registroObn350TipoQuatros;
    }

    public List<RegistroObn350TipoTres> getRegistrosObn350TipoTres() {
        return registrosObn350TipoTres;
    }

    public void setRegistrosObn350TipoTres(List<RegistroObn350TipoTres> registrosObn350TipoTres) {
        this.registrosObn350TipoTres = registrosObn350TipoTres;
    }
}
