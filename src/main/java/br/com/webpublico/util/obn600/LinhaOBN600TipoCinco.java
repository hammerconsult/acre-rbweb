package br.com.webpublico.util.obn600;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemOBN600;
import br.com.webpublico.enums.TipoDocPagto;
import br.com.webpublico.enums.TipoIdentificacaoGuia;
import br.com.webpublico.interfaces.IGuiaArquivoOBN600;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;

import java.math.BigDecimal;

/**
 * Created by RenatoRomanini on 18/05/2015.
 */
public class LinhaOBN600TipoCinco {

    public StringBuilder construirLinha(ItemOBN600 itemOBN600) {

        StringBuilder linha = new StringBuilder();
        linha.append("\r\n");
        linha.append("5");
        if (itemOBN600.getBancoObn().isArquivoCaixaEconomica()) {
            linha.append(StringUtil.cortarOuCompletarEsquerda("", 5, " "));
        } else {
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoDaAgenciaDevolucaPorErro(), 5, "0"));
        }
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoOrgaoUnidadeUGEmitente(), 11, "0"));
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoBorderoRelacaoPtos(), 11, "0"));
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getNumeroDoMovimento(), 11, "0"));
        linha.append(itemOBN600.getDataDaOrdemBancaria());
        linha.append(StringUtil.cortarOuCompletarEsquerda("", 4, " "));
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getTipoOperacaoPagto().getNumero(), 2, "0"));
        linha.append(StringUtil.cortarOuCompletarEsquerda("", 1, " "));

        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getSequenciaInternaGuia().toString(), 6, "0"));
        linha.append(StringUtil.cortarOuCompletarEsquerda("", 3, " "));
        linha.append(StringUtil.cortarOuCompletarEsquerda((itemOBN600.getGuia().getValorTotalPorGuia().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
        linha.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(itemOBN600.getGuia().getDataPagamento(), "ddMMyyyy"), 8, " "));
        linha.append(StringUtil.cortarOuCompletarEsquerda("", 7, " "));
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getTipoDocPagto().getCodigo(), 1, " "));

        montarLinhaConformeTipoGuia(itemOBN600.getGuia(), linha, itemOBN600);

        Pessoa fornecedor = itemOBN600.getGuia().getPessoa();
        linha.append(fornecedor instanceof PessoaFisica ? "02" : "01");
        String codigoIdentificacao = "";
        if (itemOBN600.getGuia().getTipoIdentificacaoGuia().equals(TipoIdentificacaoGuia.CNPJ) || itemOBN600.getGuia().getTipoIdentificacaoGuia().equals(TipoIdentificacaoGuia.CPF)) {
            codigoIdentificacao = itemOBN600.limpaCpfCnpj(fornecedor.getCpf_Cnpj());
        } else {
            codigoIdentificacao = itemOBN600.limpaCpfCnpj(itemOBN600.getGuia().getCodigoIdentificacao());
        }

        linha.append(StringUtil.cortaOuCompletaDireita(codigoIdentificacao, 14, " "));
        linha.append(StringUtil.cortaOuCompletaDireita(fornecedor.getNome(), 30, " "));

        linha.append(StringUtil.cortarOuCompletarEsquerda("", 29, " "));
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
            linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getCodigoOBdaFinalidadePagamento(itemOBN600.getFinalidadePagamento()), 3, ""));
            linha.append(StringUtil.cortaOuCompletaDireita("", 4, " "));
        }
        linha.append(StringUtil.cortaOuCompletaDireita("", 2, " "));
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getSequenciaInterna() + "", 7, "0"));

        return linha;
    }

    private String getCodigoFinalidadeParaCaixa(String codigoFinalidade) {
        return codigoFinalidade.length() > 2 ? codigoFinalidade.substring(codigoFinalidade.length() - 2, codigoFinalidade.length()) : codigoFinalidade;
    }

    private void montarLinhaConformeTipoGuia(IGuiaArquivoOBN600 guia, StringBuilder linha, ItemOBN600 item) {
        if ((TipoDocPagto.GPS).equals(item.getTipoDocPagto())) {
            GuiaGPS guiaGPS = guia.getGuiaGPS();
            linha.append(StringUtil.cortarOuCompletarEsquerda(guiaGPS.getCodigoReceitaTributo(), 6, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(guiaGPS.getCodigoIdentificacaoTributo(), 2, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(guiaGPS.getPeriodoCompetencia(), "MMyyyy"), 6, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaGPS.getValorPrevistoINSS().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaGPS.getValorOutrasEntidades().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaGPS.getAtualizacaoMonetaria().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
            linha.append(StringUtil.cortaOuCompletaDireita("", 27, " "));
        } else if ((TipoDocPagto.DARF).equals(item.getTipoDocPagto())) {
            GuiaDARF guiaDARF = guia.getGuiaDARF();
            linha.append(StringUtil.cortarOuCompletarEsquerda(guiaDARF.getCodigoReceitaTributo(), 6, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(guiaDARF.getCodigoIdentificacaoTributo(), 2, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(guiaDARF.getPeriodoApuracao(), "ddMMyyyy"), 8, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(guiaDARF.getNumeroReferencia().trim(), 17, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaDARF.getValorPrincipal().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaDARF.getValorMulta().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaDARF.getValorJuros().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(guiaDARF.getDataVencimento(), "ddMMyyyy"), 8, "0"));
        } else if ((TipoDocPagto.DARF_SIMPLES).equals(item.getTipoDocPagto())) {
            GuiaDARFSimples guiaDARFSimples = guia.getGuiaDARFSimples();
            linha.append(StringUtil.cortarOuCompletarEsquerda(guiaDARFSimples.getCodigoReceitaTributo(), 6, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(guiaDARFSimples.getCodigoIdentificacaoTributo(), 2, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(DataUtil.getDataFormatada(guiaDARFSimples.getPeriodoApuracao(), "ddMMyyyy"), 8, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaDARFSimples.getValorReceitaBruta().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaDARFSimples.getPercentualReceitaBruta() + "").replace(".", ""), 7, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaDARFSimples.getValorPrincipal().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaDARFSimples.getValorMulta().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda((guiaDARFSimples.getValorJuros().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
            linha.append(StringUtil.cortaOuCompletaDireita("", 1, " "));
        }
    }
}
