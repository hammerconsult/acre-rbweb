/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoOperacaoBensMoveisDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author reidocrime
 */
public enum TipoOperacaoBensMoveis {

    ALIENACAO_BENS_MOVEIS("Alienação de bens móveis", TipoOperacaoBensMoveisDTO.ALIENACAO_BENS_MOVEIS),
    AMORTIZACAO_BENS_MOVEIS("Amortização de bens móveis", TipoOperacaoBensMoveisDTO.AMORTIZACAO_BENS_MOVEIS),
    AQUISICAO_BENS_MOVEIS("Aquisição de bens móveis", TipoOperacaoBensMoveisDTO.AQUISICAO_BENS_MOVEIS),
    APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_DEPRECIACAO("Apuração do valor líquido contábil de bens móveis - Depreciação", TipoOperacaoBensMoveisDTO.APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_DEPRECIACAO),
    APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_AMORTIZACAO("Apuração do valor líquido contábil de bens móveis - Amortização", TipoOperacaoBensMoveisDTO.APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_AMORTIZACAO),
    APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_REDUCAO_VALOR_RECUPERAVEL("Apuração do valor líquido contábil de bens móveis - Redução ao valor recuperável", TipoOperacaoBensMoveisDTO.APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_REDUCAO_VALOR_RECUPERAVEL),
    APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_EXAUSTAO("Apuração do valor líquido contábil de bens móveis - Exaustão", TipoOperacaoBensMoveisDTO.APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_EXAUSTAO),
    AJUSTE_BENS_MOVEIS_AUMENTATIVO("Ajuste de bens móveis aumentativo", TipoOperacaoBensMoveisDTO.AJUSTE_BENS_MOVEIS_AUMENTATIVO),
    AJUSTE_BENS_MOVEIS_AUMENTATIVO_EMPRESA_PUBLICA("Ajuste de bens móveis Aumentativo - Empresa Pública", TipoOperacaoBensMoveisDTO.AJUSTE_BENS_MOVEIS_AUMENTATIVO_EMPRESA_PUBLICA),
    AJUSTE_BENS_MOVEIS_DEPRECIACAO_AUMENTATIVO("Ajuste de bens móveis - Depreciação - Aumentativo", TipoOperacaoBensMoveisDTO.AJUSTE_BENS_MOVEIS_DEPRECIACAO_AUMENTATIVO),
    AJUSTE_BENS_MOVEIS_DIMINUTIVO("Ajuste de bens móveis diminutivo", TipoOperacaoBensMoveisDTO.AJUSTE_BENS_MOVEIS_DIMINUTIVO),
    AJUSTE_BENS_MOVEIS_DIMINUTIVO_EMPRESA_PUBLICA("Ajuste de bens móveis Diminutivo - Empresa Pública", TipoOperacaoBensMoveisDTO.AJUSTE_BENS_MOVEIS_DIMINUTIVO_EMPRESA_PUBLICA),
    AJUSTE_BENS_MOVEIS_DEPRECIACAO_DIMINUTIVO("Ajuste de bens móveis - Depreciação - Diminutivo", TipoOperacaoBensMoveisDTO.AJUSTE_BENS_MOVEIS_DEPRECIACAO_DIMINUTIVO),
    AJUSTE_BENS_MOVEIS_AMORTIZACAO_AUMENTATIVO("Ajuste de bens móveis - Amortização - Aumentativo", TipoOperacaoBensMoveisDTO.AJUSTE_BENS_MOVEIS_AMORTIZACAO_AUMENTATIVO),
    AJUSTE_BENS_MOVEIS_AMORTIZACAO_DIMINUTIVO("Ajuste de bens móveis - Amortização - Diminutivo", TipoOperacaoBensMoveisDTO.AJUSTE_BENS_MOVEIS_AMORTIZACAO_DIMINUTIVO),
    DEPRECIACAO_BENS_MOVEIS("Depreciação de bens móveis", TipoOperacaoBensMoveisDTO.DEPRECIACAO_BENS_MOVEIS),
    DESINCORPORACAO_BENS_MOVEIS("Desincorporação de bens móveis", TipoOperacaoBensMoveisDTO.DESINCORPORACAO_BENS_MOVEIS),
    EXAUSTAO_BENS_MOVEIS("Exaustão de bens móveis", TipoOperacaoBensMoveisDTO.EXAUSTAO_BENS_MOVEIS),
    GANHOS_ALIENACAO_BENS_MOVEIS("Ganhos com alienação de bens móveis", TipoOperacaoBensMoveisDTO.GANHOS_ALIENACAO_BENS_MOVEIS),
    INCORPORACAO_BENS_MOVEIS("Incorporação de bens móveis", TipoOperacaoBensMoveisDTO.INCORPORACAO_BENS_MOVEIS),
    PERDAS_ALIENACAO_BENS_MOVEIS("Perdas com alienação de bens móveis", TipoOperacaoBensMoveisDTO.PERDAS_ALIENACAO_BENS_MOVEIS),
    REAVALIACAO_BENS_MOVEIS_AUMENTATIVA("Reavaliação de bens móveis aumentativa", TipoOperacaoBensMoveisDTO.REAVALIACAO_BENS_MOVEIS_AUMENTATIVA),
    REAVALIACAO_BENS_MOVEIS_DIMINUTIVA("Reavaliação de bens móveis diminutiva", TipoOperacaoBensMoveisDTO.REAVALIACAO_BENS_MOVEIS_DIMINUTIVA),
    REDUCAO_VALOR_RECUPERAVEL_BENS_MOVEIS("Redução ao valor recuperável de bens móveis", TipoOperacaoBensMoveisDTO.REDUCAO_VALOR_RECUPERAVEL_BENS_MOVEIS),
    TRANFERENCIA_BENS_MOVEIS_RECEBIDA("Transferência de bens móveis recebida", TipoOperacaoBensMoveisDTO.TRANFERENCIA_BENS_MOVEIS_RECEBIDA),
    TRANFERENCIABENS_MOVEIS_CONCEDIDA("Transferência de bens móveis concedida", TipoOperacaoBensMoveisDTO.TRANFERENCIABENS_MOVEIS_CONCEDIDA),
    TRANFERENCIABENS_MOVEIS_DEPRECIACAO_RECEBIDA("Transferência de bens móveis depreciação recebida", TipoOperacaoBensMoveisDTO.TRANFERENCIABENS_MOVEIS_DEPRECIACAO_RECEBIDA),
    TRANFERENCIABENS_MOVEIS_DEPRECIACAO_CONCEDIDA("Transferência de bens móveis depreciação concedida", TipoOperacaoBensMoveisDTO.TRANFERENCIABENS_MOVEIS_DEPRECIACAO_CONCEDIDA),
    TRANFERENCIABENS_MOVEIS_AMORTIZACAO_RECEBIDA("Transferência de bens móveis amortização recebida", TipoOperacaoBensMoveisDTO.TRANFERENCIABENS_MOVEIS_AMORTIZACAO_RECEBIDA),
    TRANFERENCIABENS_MOVEIS_AMORTIZACAO_CONCEDIDA("Transferência de bens móveis amortização concedida", TipoOperacaoBensMoveisDTO.TRANFERENCIABENS_MOVEIS_AMORTIZACAO_CONCEDIDA),
    TRANFERENCIABENS_MOVEIS_EXAUSTAO_RECEBIDA("Transferência de bens móveis exaustão recebida", TipoOperacaoBensMoveisDTO.TRANFERENCIABENS_MOVEIS_EXAUSTAO_RECEBIDA),
    TRANFERENCIABENS_MOVEIS_EXAUSTAO_CONCEDIDA("Transferência de bens móveis exaustão concedida", TipoOperacaoBensMoveisDTO.TRANFERENCIABENS_MOVEIS_EXAUSTAO_CONCEDIDA),
    TRANFERENCIABENS_MOVEIS_REDUCAO_RECEBIDA("Transferência de bens móveis redução recebida", TipoOperacaoBensMoveisDTO.TRANFERENCIABENS_MOVEIS_REDUCAO_RECEBIDA),
    TRANFERENCIABENS_MOVEIS_REDUCAO_CONCEDIDA("Transferência de bens móveis redução concedida", TipoOperacaoBensMoveisDTO.TRANFERENCIABENS_MOVEIS_REDUCAO_CONCEDIDA);

    private String descricao;
    private TipoOperacaoBensMoveisDTO toDto;

    TipoOperacaoBensMoveis(String descricao, TipoOperacaoBensMoveisDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public static List<TipoOperacaoBensMoveis> getOperacoesTransferenciaRecebida() {
        List<TipoOperacaoBensMoveis> retorno = Lists.newArrayList();
        retorno.add(TRANFERENCIA_BENS_MOVEIS_RECEBIDA);
        retorno.add(TRANFERENCIABENS_MOVEIS_DEPRECIACAO_RECEBIDA);
        retorno.add(TRANFERENCIABENS_MOVEIS_AMORTIZACAO_RECEBIDA);
        retorno.add(TRANFERENCIABENS_MOVEIS_EXAUSTAO_RECEBIDA);
        retorno.add(TRANFERENCIABENS_MOVEIS_REDUCAO_RECEBIDA);
        return retorno;
    }

    public static List<TipoOperacaoBensMoveis> getOperacoesTransferenciaConcedida() {
        List<TipoOperacaoBensMoveis> retorno = Lists.newArrayList();
        retorno.add(TRANFERENCIABENS_MOVEIS_CONCEDIDA);
        retorno.add(TRANFERENCIABENS_MOVEIS_DEPRECIACAO_CONCEDIDA);
        retorno.add(TRANFERENCIABENS_MOVEIS_AMORTIZACAO_CONCEDIDA);
        retorno.add(TRANFERENCIABENS_MOVEIS_EXAUSTAO_CONCEDIDA);
        retorno.add(TRANFERENCIABENS_MOVEIS_REDUCAO_CONCEDIDA);
        return retorno;
    }

    public static List<TipoOperacaoBensMoveis> getOperacoesParaUnicoSaldoDebito() {
        List<TipoOperacaoBensMoveis> retorno = Lists.newArrayList();
        retorno.add(AJUSTE_BENS_MOVEIS_AUMENTATIVO);
        retorno.add(AJUSTE_BENS_MOVEIS_AUMENTATIVO_EMPRESA_PUBLICA);
        retorno.add(AJUSTE_BENS_MOVEIS_DEPRECIACAO_AUMENTATIVO);
        retorno.add(AJUSTE_BENS_MOVEIS_AMORTIZACAO_AUMENTATIVO);
        return retorno;
    }

    public static List<TipoOperacaoBensMoveis> getOperacoesParaUnicoSaldoCredito() {
        List<TipoOperacaoBensMoveis> retorno = Lists.newArrayList();
        retorno.add(AJUSTE_BENS_MOVEIS_DIMINUTIVO);
        retorno.add(AJUSTE_BENS_MOVEIS_DIMINUTIVO_EMPRESA_PUBLICA);
        retorno.add(AJUSTE_BENS_MOVEIS_DEPRECIACAO_DIMINUTIVO);
        retorno.add(AJUSTE_BENS_MOVEIS_AMORTIZACAO_DIMINUTIVO);
        return retorno;
    }

    public static List<TipoOperacaoBensMoveis> getOperacoesBensOriginalCredito() {
        List<TipoOperacaoBensMoveis> toReturn = Lists.newArrayList();
        toReturn.add(TipoOperacaoBensMoveis.ALIENACAO_BENS_MOVEIS);
        toReturn.add(TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_DIMINUTIVO);
        toReturn.add(TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_DIMINUTIVO_EMPRESA_PUBLICA);
        toReturn.add(TipoOperacaoBensMoveis.REAVALIACAO_BENS_MOVEIS_DIMINUTIVA);
        return toReturn;
    }

    public static List<TipoOperacaoBensMoveis> getOperacoesTransfOriginalCredito() {
        List<TipoOperacaoBensMoveis> toReturn = Lists.newArrayList();
        toReturn.add(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_CONCEDIDA);
        return toReturn;
    }

    public static List<TipoOperacaoBensMoveis> getOperacoesBensOriginalDebito() {
        List<TipoOperacaoBensMoveis> toReturn = Lists.newArrayList();
        toReturn.add(TipoOperacaoBensMoveis.INCORPORACAO_BENS_MOVEIS);
        toReturn.add(TipoOperacaoBensMoveis.AQUISICAO_BENS_MOVEIS);
        toReturn.add(TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_AUMENTATIVO);
        toReturn.add(TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_AUMENTATIVO_EMPRESA_PUBLICA);
        toReturn.add(TipoOperacaoBensMoveis.REAVALIACAO_BENS_MOVEIS_AUMENTATIVA);
        toReturn.addAll(getOperacoesTransferenciaConcedida());
        return toReturn;
    }

    public static List<TipoOperacaoBensMoveis> getOperacoesTransfOriginalDebito() {
        List<TipoOperacaoBensMoveis> toReturn = Lists.newArrayList();
        toReturn.add(TipoOperacaoBensMoveis.TRANFERENCIA_BENS_MOVEIS_RECEBIDA);
        toReturn.addAll(getOperacoesTransferenciaConcedida());
        return toReturn;
    }

    public static List<TipoOperacaoBensMoveis> getOperacoesBensAjusteCredito() {
        List<TipoOperacaoBensMoveis> toReturn = Lists.newArrayList();
        toReturn.add(TipoOperacaoBensMoveis.DEPRECIACAO_BENS_MOVEIS);
        toReturn.add(TipoOperacaoBensMoveis.AMORTIZACAO_BENS_MOVEIS);
        toReturn.add(TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_DEPRECIACAO_AUMENTATIVO);
        toReturn.add(TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_AMORTIZACAO_AUMENTATIVO);
        return toReturn;
    }

    public static List<TipoOperacaoBensMoveis> getOperacoesTransfAjusteCredito() {
        List<TipoOperacaoBensMoveis> toReturn = Lists.newArrayList();
        toReturn.add(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_DEPRECIACAO_RECEBIDA);
        toReturn.add(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_AMORTIZACAO_RECEBIDA);
        toReturn.add(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_REDUCAO_RECEBIDA);
        return toReturn;
    }

    public static List<TipoOperacaoBensMoveis> getOperacoesBensAjusteDebito() {
        List<TipoOperacaoBensMoveis> toReturn = Lists.newArrayList();
        toReturn.add(TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_DEPRECIACAO_DIMINUTIVO);
        toReturn.add(TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_AMORTIZACAO_DIMINUTIVO);
        return toReturn;
    }

    public static List<TipoOperacaoBensMoveis> getOperacoesTransfAjusteDebito() {
        List<TipoOperacaoBensMoveis> toReturn = Lists.newArrayList();
        toReturn.add(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_DEPRECIACAO_CONCEDIDA);
        toReturn.add(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_AMORTIZACAO_CONCEDIDA);
        toReturn.add(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_REDUCAO_CONCEDIDA);

        return toReturn;
    }


    public String getDescricao() {
        return descricao;
    }

    public TipoOperacaoBensMoveisDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
