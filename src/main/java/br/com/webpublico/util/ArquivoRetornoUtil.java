package br.com.webpublico.util;


import br.com.webpublico.entidades.ArquivoDAF607;
import br.com.webpublico.entidadesauxiliares.*;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ArquivoRetornoUtil implements Serializable {
    private static final BigDecimal CEM = new BigDecimal("100");
    private static final DateFormat formatter = new SimpleDateFormat("yyyyMMdd");


    public static List<ArquivoDAF607> gerarArquivoDAF607(String conteudo) throws IOException, ParseException {
        List<ArquivoDAF607> arquivos = Lists.newArrayList();

        ArquivoDAF607 arquivo = new ArquivoDAF607();
        String registro;
        StringReader sr = new StringReader(conteudo);
        BufferedReader bufferedReader = new BufferedReader(sr);
        String linha = "";

        while ((linha = bufferedReader.readLine()) != null) {
            arquivo.contarLinha();
            registro = linha.substring(0, 1);
            if (registro.equalsIgnoreCase("1")) {
                arquivo.setHeaderDAF607(gerarHeaderDAF607(linha, arquivo));
            }
            if (registro.equalsIgnoreCase("2")) {
                arquivo.adicionarRegisto(gerarRegistroDAF607(linha, arquivo));
            }
            if (registro.equalsIgnoreCase("9")) {
                arquivo.setTraillerDAF607(gerarTraillerDAF607(linha));
                arquivos.add(arquivo);
                arquivo = new ArquivoDAF607();
            }
        }
        bufferedReader.close();

        return arquivos;
    }

    public static List<ArquivoRCB001> gerarArquivoRCB001(String conteudo) throws IOException {
        List<ArquivoRCB001> arquivos = Lists.newArrayList();

        ArquivoRCB001 arquivo = new ArquivoRCB001();
        String registro;
        StringReader sr = new StringReader(conteudo);
        BufferedReader bufferedReader = new BufferedReader(sr);
        String linha = "";

        BigDecimal valorTotal = new BigDecimal(0);
        while ((linha = bufferedReader.readLine()) != null) {
            arquivo.contarLinha();

            registro = linha.substring(0, 1);
            if (registro.equalsIgnoreCase("A")) {
                arquivo.setHeader(gerarHeaderRCB001(linha));
            }
            if (registro.equalsIgnoreCase("G")) {
                arquivo.adicionarRegisto(gerarRegistroRCB001(linha));
            }
            if (registro.equalsIgnoreCase("Z")) {
                arquivo.setTrailler(gerarTraillerRCB001(linha));
                arquivos.add(arquivo);
                arquivo = new ArquivoRCB001();
            }
        }
        bufferedReader.close();

        return arquivos;
    }

    private static HeaderRCB001 gerarHeaderRCB001(String linha) {
        HeaderRCB001 headerRCB001 = new HeaderRCB001();
        headerRCB001.setCodigoRegistro(linha.substring(0, 1));
        headerRCB001.setCodigoRemessa(linha.substring(1, 2));
        headerRCB001.setCodigoConvenio(linha.substring(2, 22));
        headerRCB001.setCodigoBanco(linha.substring(42, 45));
        headerRCB001.setNomeBanco(linha.substring(45, 64));
        headerRCB001.setDataArquivo(linha.substring(65, 73));
        headerRCB001.setNumeroArquivo(linha.substring(74, 79).trim());
        return headerRCB001;
    }

    private static HeaderDAF607 gerarHeaderDAF607(String linha, ArquivoDAF607 arquivoDAF607) throws ParseException {
        HeaderDAF607 headerDAF607 = new HeaderDAF607();
        headerDAF607.setCodigoRegistro(linha.substring(0, 1));
        headerDAF607.setSequencialRegistro(linha.substring(1, 9));
        headerDAF607.setCodigoConvenio(linha.substring(9, 29));
        headerDAF607.setDataGeracaoArquivo(linha.substring(29, 37));
        headerDAF607.setNumeroRemessaSerpro(linha.substring(37, 43));
        headerDAF607.setNumeroVersao(linha.substring(43, 45));
        headerDAF607.setFiller(linha.substring(45, 66));
        headerDAF607.setUsoBancoSerpro(linha.substring(67, 75));
        headerDAF607.setCodigoBancoArrecadador(linha.substring(75, 78));
        headerDAF607.setDataCreditoConta(linha.substring(78, 86));
        arquivoDAF607.setDataCreditoConta(formatter.parse(linha.substring(78, 86)));
        return headerDAF607;
    }

    private static RegistroDAF607 gerarRegistroDAF607(String linha, ArquivoDAF607 arquivoDAF607) throws ParseException {
        RegistroDAF607 registroDAF607 = new RegistroDAF607();
        registroDAF607.setCodigoRegistro(linha.substring(0, 1));
        registroDAF607.setSequencialRegistro(linha.substring(1, 9));
        registroDAF607.setDataArrecadacaoDocumento(formatter.parse(linha.substring(9, 17)));
        registroDAF607.setDataVencimentoDocumento(formatter.parse(linha.substring(17, 25)));
        registroDAF607.setCnpjContribuinte(linha.substring(74, 88));
        registroDAF607.setEsferaReceita(linha.substring(99, 100));
        registroDAF607.setCompetenciaConstanteGuia(linha.substring(100, 106));
        registroDAF607.setValorPrincipal(new BigDecimal(linha.substring(106, 121) + "." + linha.substring(121, 123)));
        registroDAF607.setValorMulta(new BigDecimal(linha.substring(123, 138) + "." + linha.substring(138, 140)));
        registroDAF607.setValorJuros(new BigDecimal(linha.substring(140, 155) + "." + linha.substring(155, 157)));
        registroDAF607.setValorAutenticacaoGuia(linha.substring(204, 221));
        registroDAF607.setNumeroAutenticacaoBancoArrecadador(linha.substring(221, 244));
        registroDAF607.setCodigoBancoArrecadadorGuia(linha.substring(244, 247));
        registroDAF607.setPrefixoAgenciaArrecadadora(linha.substring(247, 251));
        registroDAF607.setCodigoSiafi(linha.substring(455, 461));
        registroDAF607.setCodigoIdentificadorGuia(linha.substring(461, 478));
        registroDAF607.setArquivoDAF607(arquivoDAF607);
        return registroDAF607;
    }

    private static TraillerDAF607 gerarTraillerDAF607(String linha) {
        TraillerDAF607 traillerDAF607 = new TraillerDAF607();
        traillerDAF607.setCodigoRegistro(linha.substring(0, 1));
        traillerDAF607.setSequencialRegistro(linha.substring(1, 9));
        traillerDAF607.setTotalRegistrosGravador(linha.substring(9, 15));
        traillerDAF607.setValorTotalRecebido(linha.substring(15, 30) + "." + linha.substring(30, 32));
        return traillerDAF607;
    }

    private static RegistroRCB001 gerarRegistroRCB001(String linha) {
        RegistroRCB001 registroRCB001 = new RegistroRCB001();
        registroRCB001.setCodigoRegistro(linha.substring(0, 1));
        registroRCB001.setDataPagamento(linha.substring(21, 29));
        registroRCB001.setDataCredito(linha.substring(29, 37));
        registroRCB001.setCodigoBarras(linha.substring(37, 81));
        registroRCB001.setValorRecebido(new BigDecimal(linha.substring(81, 93)).divide(CEM));
        registroRCB001.setValorTarifa(new BigDecimal(linha.substring(93, 100)).divide(CEM));
        registroRCB001.setNumeroSequencial(linha.substring(100, 108));
        registroRCB001.setCodigoAgenciaArrecadadora(linha.substring(108, 116));
        registroRCB001.setFormaArrecadacao(linha.substring(116, 117));
        return registroRCB001;
    }

    private static TraillerRCB001 gerarTraillerRCB001(String linha) {
        TraillerRCB001 traillerRCB001 = new TraillerRCB001();
        traillerRCB001.setCodigoRegistro(linha.substring(0, 1));
        traillerRCB001.setTotalRegistros(Integer.parseInt(linha.substring(1, 7)));
        traillerRCB001.setValorTotal(new BigDecimal(linha.substring(7, 24)).divide(CEM));
        return traillerRCB001;
    }

}
