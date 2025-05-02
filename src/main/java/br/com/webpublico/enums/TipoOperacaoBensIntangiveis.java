/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoOperacaoBensIntangiveisDTO;

/**
 *
 * @author reidocrime
 */
public enum TipoOperacaoBensIntangiveis {
    INCORPORACAO_BENS_INTANGIVEIS("Incorporação de bens intangíveis", TipoOperacaoBensIntangiveisDTO.INCORPORACAO_BENS_INTANGIVEIS),
    AQUISICAO_BENS_INTANGIVEIS("Aquisição", TipoOperacaoBensIntangiveisDTO.AQUISICAO_BENS_INTANGIVEIS),
    AMORTIZACAO_BENS_INTANGIVEIS("Amortização de bens intangíveis", TipoOperacaoBensIntangiveisDTO.AMORTIZACAO_BENS_INTANGIVEIS),
    ALIENACAO_BENS_INTANGIVEIS("Alienação de bens intangíveis", TipoOperacaoBensIntangiveisDTO.ALIENACAO_BENS_INTANGIVEIS),
    GANHOS_ALIENACAO_BENS_INTANGIVEIS("Ganhos com alienação de bens intangíveis", TipoOperacaoBensIntangiveisDTO.GANHOS_ALIENACAO_BENS_INTANGIVEIS),
    PERDAS_ALIENACAO_BENS_INTANGIVEIS("Perdas com alienação de bens intangíveis", TipoOperacaoBensIntangiveisDTO.PERDAS_ALIENACAO_BENS_INTANGIVEIS),
    REDUCAO_VALOR_RECUPERAVEL_BENS_INTANGIVEIS("Redução ao valor recuperável de bens intangiveis", TipoOperacaoBensIntangiveisDTO.REDUCAO_VALOR_RECUPERAVEL_BENS_INTANGIVEIS),
    APURACAO_VALOR_LIQUIDO_BENS_INTAGIVEIS_AMORTIZACAO("Apuração do valor líquido contábil de bens intangíveis - Amortização", TipoOperacaoBensIntangiveisDTO.APURACAO_VALOR_LIQUIDO_BENS_INTAGIVEIS_AMORTIZACAO),
    APURACAO_VALOR_LIQUIDO_BENS_INTAGIVEIS_REDUCAO_VALOR_RECUPERAVEL("Apuração do valor líquido contábil de bens intangíveis - Redução ao valor recuperável", TipoOperacaoBensIntangiveisDTO.APURACAO_VALOR_LIQUIDO_BENS_INTAGIVEIS_REDUCAO_VALOR_RECUPERAVEL),
    TRANSFERENCIA_BENS_INTANGIVEIS_RECEBIDA("Transferência de bens intangíveis recebida", TipoOperacaoBensIntangiveisDTO.TRANSFERENCIA_BENS_INTANGIVEIS_RECEBIDA),
    TRANSFERENCIA_BENS_INTANGIVEIS_CONCEDIDA("Transferência de bens intangíveis concedida", TipoOperacaoBensIntangiveisDTO.TRANSFERENCIA_BENS_INTANGIVEIS_CONCEDIDA),
    TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_RECEBIDA("Transferência de Intangível - Amortização Recebida", TipoOperacaoBensIntangiveisDTO.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_RECEBIDA),
    TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_CONCEDIDA("Transferência de Intangível - Amortização Concedida", TipoOperacaoBensIntangiveisDTO.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_CONCEDIDA),
    DESINCORPORACAO_BENS_INTANGIVEIS("Desincorporação de bens intangíveis", TipoOperacaoBensIntangiveisDTO.DESINCORPORACAO_BENS_INTANGIVEIS);
    private String descricao;
    private TipoOperacaoBensIntangiveisDTO toDto;

    TipoOperacaoBensIntangiveis(String descricao, TipoOperacaoBensIntangiveisDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoOperacaoBensIntangiveisDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
