package br.com.webpublico.util.obn600;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidadesauxiliares.ItemOBN600;
import br.com.webpublico.util.StringUtil;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 20/05/14
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public class LinhaOBN600TipoDois {

    public StringBuilder construirLinha(ItemOBN600 itemOBN600) {

        StringBuilder linha = new StringBuilder();
        linha.append("\r\n");
        linha.append("2");
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
        linha.append(itemOBN600.getBancoObn().isArquivoCaixaEconomica() ? " " : "0");
        linha.append(StringUtil.cortarOuCompletarEsquerda("", 9, "0"));
        linha.append(StringUtil.cortarOuCompletarEsquerda((itemOBN600.getValor().setScale(2, BigDecimal.ROUND_HALF_UP) + "").replace(".", ""), 17, "0"));
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getBancoFornecedor() == null ? "" : itemOBN600.getBancoFornecedor().getNumeroBanco(), 3, "0"));
        if (itemOBN600.getBancoObn().isArquivoCaixaEconomica()) {
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getAgenciaFornecedor() == null ? "" : itemOBN600.getAgenciaFornecedor().getNumeroAgencia(), 4, "0"));
            linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getAgenciaFornecedor() == null ? "" : itemOBN600.getAgenciaFornecedor().getDigitoVerificador(), 1, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoContaCorrenteFornecedor(), 13, "0"));
            linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getFornecedor().getNome(), 45, " "));
            linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getEndereco() == null ? "" : itemOBN600.getEndereco().getLogradouro(), 62, " "));
        } else {
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getAgenciaFornecedor() == null ? "" : itemOBN600.getAgenciaFornecedor().getNumeroAgencia(), 4, "0"));
            linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getAgenciaFornecedor() == null ? "" : itemOBN600.getAgenciaFornecedor().getDigitoVerificador(), 1, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoContaCorrenteFornecedor(), 10, "0"));
            linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getFornecedor().getNome(), 45, " "));
            linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getEndereco() == null ? "" : itemOBN600.getEndereco().getLogradouro(), 65, " "));
        }
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getEndereco() == null ? "" : itemOBN600.getEndereco().getLocalidade(), 28, " "));
        linha.append(StringUtil.cortaOuCompletaDireita("", 17, " "));
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getCep(itemOBN600.getEndereco()), 8, " "));
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getUf(), 2, " "));
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getHistorico(), 40, " "));
        linha.append("0");
        linha.append(itemOBN600.getFornecedor() instanceof PessoaFisica ? "2" : "1");
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.limpaCpfCnpj(itemOBN600.getFornecedor().getCpf_Cnpj()), 14, " "));
        if (itemOBN600.getBancoObn().isArquivoCaixaEconomica()) {
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getContaUnica() ? "" : itemOBN600.getCodigoDaAgenciaDevolucaPorErro(), 5, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getContaUnica() ? "" : itemOBN600.getCodigoDaContaDevolucaPorErro(), 13, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(getCodigoFinalidadeParaCaixa(itemOBN600.getCodigoOBdaFinalidadePagamento(itemOBN600.getFinalidadePagamento())), 3, "0"));
            linha.append(StringUtil.cortaOuCompletaDireita("", 1, " "));
        } else {
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getContaUnica() ? "" : itemOBN600.getCodigoDaAgenciaDevolucaPorErro(), 5, "0"));
            linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getContaUnica() ? "" : itemOBN600.getCodigoDaContaDevolucaPorErro(), 10, "0"));
            linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getCodigoOBdaFinalidadePagamento(itemOBN600.getFinalidadePagamento()), 3, "0"));
            linha.append(StringUtil.cortaOuCompletaDireita("", 4, " "));
        }
        linha.append(StringUtil.cortaOuCompletaDireita("", 2, itemOBN600.getBancoObn().isArquivoCaixaEconomica() ? " " : "0"));
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getSequenciaInterna() + "", 7, "0"));
        return linha;
    }

    private String getCodigoFinalidadeParaCaixa(String codigoFinalidade) {
        return codigoFinalidade.length() > 2 ? codigoFinalidade.substring(codigoFinalidade.length() - 2, codigoFinalidade.length()) : codigoFinalidade;
    }
}
