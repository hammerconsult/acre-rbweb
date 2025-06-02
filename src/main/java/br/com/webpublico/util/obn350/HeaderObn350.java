/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.obn350;

import br.com.webpublico.enums.TipoBancoArquivoObn;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.io.Serializable;

/**
 * @author reidocrime
 */
public class HeaderObn350 implements Serializable {

    private String linha;
    private String zeros;
    private String anoArquivo;
    private String dataGeracaoArquivo;
    private String horaGeracaoArquivo;
    private String numeroRemessaConsecutiva;
    private String campo20b001;
    private String numeroContrato;
    private String numeroRetornoDaRemessa;
    private String codigoRetornoRemessa;
    private String motivoDevolucaoRemessa;
    private String tipoDeArquivo;
    private String brancos;
    private String numeroSequencial;

    public HeaderObn350() {
        throw new ExcecaoNegocioGenerica("Construtor não suportado, utilize o construtor informando uma linha \"Texto\" como paramentro!");
    }

    public HeaderObn350(String linha) {
        this.linha = linha;
        validaLinha(this.linha);
    }

    private void validaLinha(String linha) {
        if (linha == null) {
            throw new ExcecaoNegocioGenerica("O valor da linha para o header esta nula!");
        }
        if (linha.equals("")) {
            throw new ExcecaoNegocioGenerica("A linha para o header esta em Branco!");
        }
        if (linha.length() != 350) {
            throw new ExcecaoNegocioGenerica(" A linhas não atende o formato exigido pelo header, deveria ter 350 char e contem " + linha.length() + "!");
        }

    }

    public String getLinha() {
        return linha;
    }

    public String getZeros() {
        zeros = linha.substring(0, 35);
        return zeros;
    }

    public String getDataGeracaoArquivo() {
        dataGeracaoArquivo = linha.substring(35, 43);
        return dataGeracaoArquivo.substring(0, 2) + "/" + dataGeracaoArquivo.substring(2, 4) + "/" + dataGeracaoArquivo.substring(4, 8);
    }

    public String getAnoDoArquivo() {
        anoArquivo = linha.substring(39, 43);
        return anoArquivo;
    }

    public String getHoraGeracaoArquivo() {
        horaGeracaoArquivo = linha.substring(43, 47);
        return horaGeracaoArquivo.substring(0, 2) + ":" + horaGeracaoArquivo.substring(2, 4);
    }

    public String getNumeroRemessaConsecutiva() {
        numeroRemessaConsecutiva = linha.substring(47, 52);
        return numeroRemessaConsecutiva;
    }

    public String getCampo20b001() {
        campo20b001 = linha.substring(52, 58);
        return campo20b001;
    }

    public String getNumeroContrato() {
        numeroContrato = linha.substring(58, 67);
        return numeroContrato;
    }

    public String getNumeroRetornoDaRemessa() {
        numeroRetornoDaRemessa = linha.substring(67, 72);
        return numeroRetornoDaRemessa;
    }

    public String getCodigoRetornoRemessa() {
        codigoRetornoRemessa = linha.substring(72, 74);
        return codigoRetornoRemessa;
    }

    public String getMotivoDevolucaoRemessa() {
        motivoDevolucaoRemessa = linha.substring(74, 118);
        return motivoDevolucaoRemessa;
    }

    public String getTipoDeArquivo() {
        tipoDeArquivo = linha.substring(118, 124);
        return tipoDeArquivo;
    }

    public String getBrancos() {
        brancos = linha.substring(124, 343);
        return brancos;
    }

    public String getNumeroSequencial() {
        numeroSequencial = linha.substring(343, 350);
        return numeroSequencial;
    }

    private TipoBancoArquivoObn getTipoBanco() {
        if (campo20b001.endsWith("001")) {
            return TipoBancoArquivoObn.BANCO_DO_BRASIL;
        } else if (campo20b001.endsWith("104")) {
            return TipoBancoArquivoObn.CAIXA_ECONOMICA;
        }
        return null;
    }

    public boolean isArquivoCaixaEconomica() {
        return TipoBancoArquivoObn.CAIXA_ECONOMICA.equals(getTipoBanco());
    }

    public boolean isArquivoBancoDoBrasil() {
        return TipoBancoArquivoObn.BANCO_DO_BRASIL.equals(getTipoBanco());
    }
}
