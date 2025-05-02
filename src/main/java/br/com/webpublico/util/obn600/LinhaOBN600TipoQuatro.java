package br.com.webpublico.util.obn600;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemOBN600;
import br.com.webpublico.enums.TipoDocPagto;
import br.com.webpublico.interfaces.IGuiaArquivoOBN600;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;

import java.math.BigDecimal;

/**
 * Created by RenatoRomanini on 18/05/2015.
 */
public class LinhaOBN600TipoQuatro {

    public StringBuilder construirLinha(ItemOBN600 itemOBN600) {
        StringBuilder linha = new StringBuilder();
        linha.append("\r\n");
        linha.append("4");
        if (itemOBN600.getBancoObn().isArquivoCaixaEconomica()) {
            linha.append(StringUtil.cortarOuCompletarEsquerda("", 5, " "));
        } else {
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoDaAgenciaDevolucaPorErro(), 5, "0"));
        }//6
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoOrgaoUnidadeUGEmitente(), 11, "0"));//17
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoBorderoRelacaoPtos(), 11, "0"));//28
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getNumeroDoMovimento(), 11, "0"));//39
        linha.append(itemOBN600.getDataDaOrdemBancaria());//47
        linha.append(StringUtil.cortarOuCompletarEsquerda("", 4, " "));//51
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getTipoOperacaoPagto().getNumero(), 2, "0"));//53
        linha.append(StringUtil.cortarOuCompletarEsquerda("", 1, " "));//54
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getSequenciaInternaGuia().toString(), 6, "0"));//60
        linha.append(StringUtil.cortarOuCompletarEsquerda("", 3, " "));//63
        linha.append(StringUtil.cortarOuCompletarEsquerda((itemOBN600.getGuia().getValorTotalPorGuia().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));//80
        linha.append(StringUtil.cortarOuCompletarEsquerda("", 15, " "));//95
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getTipoDocPagto().getCodigo(), 1, " "));//96

        montarLinhaConformeTipoGuia(itemOBN600, linha);

        if (itemOBN600.getBancoObn().isArquivoBancoDoBrasil() || itemOBN600.getBancoObn().isArquivoCaixaEconomica()) {
            if (TipoDocPagto.GRU.equals(itemOBN600.getTipoDocPagto())) {
                linha.append(itemOBN600.getFornecedor() instanceof PessoaFisica ? "2" : "1");//200
                linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.limpaCpfCnpj(itemOBN600.getFornecedor().getCpf_Cnpj()), 14, " "));//214
                linha.append(StringUtil.cortaOuCompletaDireita("", 11, " "));//225
                linha.append(StringUtil.cortarOuCompletarEsquerda("", 11, "0"));//236
                linha.append(StringUtil.cortarOuCompletarEsquerda("", 11, "0"));//247
                linha.append(StringUtil.cortarOuCompletarEsquerda("", 16, " "));//263
            } else {
                linha.append(itemOBN600.getFornecedor() instanceof PessoaFisica ? "2" : "1");//200
                linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.limpaCpfCnpj(itemOBN600.getFornecedor().getCpf_Cnpj()), 14, " "));//214
                linha.append(StringUtil.cortarOuCompletarEsquerda("", 49, " "));//263
            }
        } else {
            linha.append(StringUtil.cortarOuCompletarEsquerda("", 64, " "));//263
        }
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getHistorico(), 40, " "));
        linha.append(StringUtil.cortarOuCompletarEsquerda("", 16, " "));
        if (itemOBN600.getBancoObn().isArquivoCaixaEconomica()) {
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getContaUnica() ? "" : itemOBN600.getCodigoDaAgenciaDevolucaPorErro(), 5, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getContaUnica() ? "" : itemOBN600.getCodigoDaContaDevolucaPorErro(), 13, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(getCodigoFinalidadeParaCaixa(itemOBN600.getCodigoOBdaFinalidadePagamento(itemOBN600.getFinalidadePagamento())), 3, "0"));
            linha.append(StringUtil.cortaOuCompletaDireita("", 1, " "));
        } else {
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getContaUnica() ? "" : itemOBN600.getCodigoDaAgenciaDevolucaPorErro(), 5, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getContaUnica() ? "" : itemOBN600.getCodigoDaContaDevolucaPorErro(), 10, "0"));
            linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getCodigoOBdaFinalidadePagamento(itemOBN600.getFinalidadePagamento()), 3, " "));
            linha.append(StringUtil.cortaOuCompletaDireita("", 4, " "));
        }
        linha.append(StringUtil.cortaOuCompletaDireita("", 2, itemOBN600.getBancoObn().isArquivoCaixaEconomica() ? " " : "0"));
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getSequenciaInterna() + "", 7, "0"));

        return linha;
    }

    private String getCodigoFinalidadeParaCaixa(String codigoFinalidade) {
        return codigoFinalidade.length() > 2 ? codigoFinalidade.substring(codigoFinalidade.length() - 2, codigoFinalidade.length()) : codigoFinalidade;
    }

    private void montarLinhaConformeTipoGuia(ItemOBN600 item, StringBuilder linha) {
        IGuiaArquivoOBN600 guia = item.getGuia();
        if (TipoDocPagto.FATURA.equals(item.getTipoDocPagto())) {
            GuiaFatura guiaFatura = guia.getGuiaFatura();
            linha.append(StringUtil.cortaOuCompletaDireita(montarCodigoBarrasGuiaFatura(guiaFatura.getCodigoBarra(), item), 44, " "));
            linha.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(guiaFatura.getDataVencimento(), "ddMMyyyy"), 8, " "));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaFatura.getValorNominal().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaFatura.getValorDescontos().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaFatura.getValorJuros().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
        } else if (TipoDocPagto.CONVENIO.equals(item.getTipoDocPagto())) {
            GuiaConvenio guiaConvenio = guia.getGuiaConvenio();
            linha.append(StringUtil.cortaOuCompletaDireita(montarCodigoBarrasGuiaConvenio(guiaConvenio.getCodigoBarra()), 44, " "));
            linha.append(StringUtil.cortaOuCompletaDireita("", 59, " "));
        } else if (TipoDocPagto.GRU.equals(item.getTipoDocPagto())) {
            GuiaGRU guiaGRU = guia.getGuiaGRU();
            linha.append(StringUtil.cortaOuCompletaDireita(montarCodigoBarrasGuiaConvenio(guiaGRU.getCodigoBarra()), 44, " "));//140
            linha.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(guiaGRU.getCompetencia(), "MMyyyy"), 6, " "));//146
            linha.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(guiaGRU.getVencimento(), "ddMMyyyy"), 8, " "));//154
            linha.append(StringUtil.cortarOuCompletarEsquerda(guiaGRU.getNumeroReferencia(), 20, " "));//174
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaGRU.getValorPrincipal().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 11, "0"));//185
            linha.append(StringUtil.cortarOuCompletarEsquerda("", 11, "0"));//196
            linha.append(StringUtil.cortaOuCompletaDireita("", 3, " "));//199
        }
    }

    private String montarCodigoBarrasGuiaConvenio(String codigoBarra) {
        String retorno = "";
        codigoBarra = codigoBarra.replace(" ", "");
        retorno += codigoBarra.substring(0, 11);
        retorno += codigoBarra.substring(13, 24);
        retorno += codigoBarra.substring(26, 37);
        retorno += codigoBarra.substring(39, 50);
        return retorno;
    }


    private String montarCodigoBarrasGuiaFatura(String codigoBarra, ItemOBN600 itemOBN600) {
        String retorno = "";
        //CÓDIGO COMPLETO: 00190.00009 03268.716002 25449.950176 1 86320079007149
        //CÒDIGO         : 00190     9  3268 716002 25449 950176 1 86320079007149
        codigoBarra = codigoBarra.replace(" ", "");
        codigoBarra = codigoBarra.replace(".", "");
        if (itemOBN600.getBancoObn().isArquivoCaixaEconomica()) {
            retorno += codigoBarra.substring(0, 4);
            retorno += codigoBarra.substring(32, codigoBarra.length());
        } else {
            if (codigoBarra.length() != 44) {
                retorno += codigoBarra.substring(0, 4);
                retorno += codigoBarra.substring(32, codigoBarra.length());
                retorno += codigoBarra.substring(4, 9);
                retorno += codigoBarra.substring(10, 20);
                retorno += codigoBarra.substring(21, 31);
            } else {
                retorno = codigoBarra;
            }
        }
        return retorno;
    }
}
