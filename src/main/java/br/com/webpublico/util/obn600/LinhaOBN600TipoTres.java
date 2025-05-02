package br.com.webpublico.util.obn600;

import br.com.webpublico.entidadesauxiliares.ItemOBN600;
import br.com.webpublico.util.StringUtil;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 15/02/16
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public class LinhaOBN600TipoTres {

    public StringBuilder construirLinha(ItemOBN600 itemOBN600) {
        StringBuilder linha = new StringBuilder();
        linha.append("\r\n");
        linha.append("3");
        //002 até 006
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoDaAgenciaDevolucaPorErro(), 5, "0"));
        //007 até 017
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoOrgaoUnidadeUGEmitente(), 11, "0"));
        //018 até 028
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoBorderoRelacaoPtos(), 11, "0"));
        //029 até 039
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getNumeroDoMovimento(), 11, "0"));
        //040 até 047
        linha.append(itemOBN600.getDataDaOrdemBancaria());
        //048 até 051
        linha.append(StringUtil.cortarOuCompletarEsquerda("", 4, " "));
        //052 até 053
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getTipoOperacaoPagto().getNumero(), 2, "0"));
        //054 até 054
        linha.append("1");
        //055 até 060
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getSequenciaInternoMovimentosOB().toString(), 6, "0"));
        //061 até 063
        linha.append(StringUtil.cortarOuCompletarEsquerda("", 3, " "));
        //064 até 080
        linha.append(StringUtil.cortarOuCompletarEsquerda((itemOBN600.getValor().setScale(2, BigDecimal.ROUND_HALF_UP).toString()).replace(".", ""), 17, "0"));
        //081 até 083
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getBancoFornecedor() == null ? "" : itemOBN600.getBancoFornecedor().getNumeroBanco(), 3, "0"));
        //084 até 087
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getAgenciaFornecedor() == null ? "" : itemOBN600.getAgenciaFornecedor().getNumeroAgencia(), 4, "0"));
        //088 até 088
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getAgenciaFornecedor() == null ? "" : itemOBN600.getAgenciaFornecedor().getDigitoVerificador(), 1, "0"));
        //089 até 098
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getCodigoContaCorrenteFornecedor(), 10, "0"));
        //099 até 143
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getFornecedor().getNome(), 45, " "));
        //144 até 208
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getEndereco() == null ? "" : itemOBN600.getEndereco().getLogradouro(), 65, " "));
        //209 até 236
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getEndereco() == null ? "" : itemOBN600.getEndereco().getLocalidade(), 28, " "));
        //237 até 253
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getGuia().getGuiaGRU().getUgGestao().replace("/", "") + itemOBN600.getGuia().getGuiaGRU().getCodigoRecolhimento().replace("-", ""), 17, " "));
        //254 até 261
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getEndereco() == null ? "" : itemOBN600.getEndereco().getCep() == null ? "" : itemOBN600.getEndereco().getCep().replace("-", "").trim(), 8, " "));
        //262 até 263
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getUf(), 2, " "));
        //264 até 303
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getHistorico(), 40, " "));
        //304 até 304
        linha.append("0");
        //305 até 305
        linha.append("5");
        //306 até 319
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.limpaCpfCnpj(itemOBN600.getFornecedor().getCpf_Cnpj()), 14, " "));
        //320 até 324
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getContaUnica() ? "" : itemOBN600.getCodigoDaAgenciaDevolucaPorErro(), 5, "0"));
        //325 até 334
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getContaUnica() ? "" : itemOBN600.getCodigoDaContaDevolucaPorErro(), 10, "0"));
        //335 até 347
        linha.append(StringUtil.cortaOuCompletaDireita(itemOBN600.getCodigoOBdaFinalidadePagamento(itemOBN600.getFinalidadePagamento()), 3, "0"));
        //338 até 341
        linha.append(StringUtil.cortaOuCompletaDireita("", 4, " "));
        //342 até 343
        linha.append(StringUtil.cortaOuCompletaDireita("", 2, "0"));
        //344 até 350
        linha.append(StringUtil.cortarOuCompletarEsquerda(itemOBN600.getSequenciaInterna().toString(), 7, "0"));
        return linha;
    }
}
