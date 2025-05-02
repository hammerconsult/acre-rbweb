/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoOperacaoBensImoveisDTO;

/**
 *
 * @author reidocrime
 */
public enum TipoOperacaoBensImoveis {

    ALIENACAO_BENS_IMOVEIS("Alienação de bens Imóveis", TipoOperacaoBensImoveisDTO.ALIENACAO_BENS_IMOVEIS),
    AMORTIZACAO_BENS_IMOVEIS("Amortização de bens imóveis", TipoOperacaoBensImoveisDTO.AMORTIZACAO_BENS_IMOVEIS),
    DEPRECIACAO_BENS_IMOVEIS("Depreciação de bens imóveis", TipoOperacaoBensImoveisDTO.DEPRECIACAO_BENS_IMOVEIS),
    AJUSTE_BENS_IMOVEIS_AUMENTATIVO("Ajuste de bens imóveis aumentativo", TipoOperacaoBensImoveisDTO.AJUSTE_BENS_IMOVEIS_AUMENTATIVO),
    AJUSTE_BENS_IMOVEIS_AUMENTATIVO_EMPRESA_PUBLICA("Ajuste de bens imóveis Aumentativo - Empresa Pública", TipoOperacaoBensImoveisDTO.AJUSTE_BENS_IMOVEIS_AUMENTATIVO_EMPRESA_PUBLICA),
    AJUSTE_BENS_IMOVEIS_DIMINUTIVO("Ajuste de bens imóveis diminutivo", TipoOperacaoBensImoveisDTO.AJUSTE_BENS_IMOVEIS_DIMINUTIVO),
    AJUSTE_BENS_IMOVEIS_DIMINUTIVO_EMPRESA_PUBLICA("Ajuste de bens imóveis Diminutivo - Empresa Pública", TipoOperacaoBensImoveisDTO.AJUSTE_BENS_IMOVEIS_DIMINUTIVO_EMPRESA_PUBLICA),
    AJUSTE_BENS_IMOVEIS_DEPRECIACAO_AUMENTATIVO("Ajuste de bens imóveis - Depreciação - Aumentativo", TipoOperacaoBensImoveisDTO.AJUSTE_BENS_IMOVEIS_DEPRECIACAO_AUMENTATIVO),
    AJUSTE_BENS_IMOVEIS_DEPRECIACAO_DIMINUTIVO("Ajuste de bens imóveis - Depreciação - Diminutivo", TipoOperacaoBensImoveisDTO.AJUSTE_BENS_IMOVEIS_DEPRECIACAO_DIMINUTIVO),
    AJUSTE_BENS_IMOVEIS_AMORTIZACAO_AUMENTATIVO("Ajuste de bens imóveis - Amortização  - Aumentativo", TipoOperacaoBensImoveisDTO.AJUSTE_BENS_IMOVEIS_AMORTIZACAO_AUMENTATIVO),
    AJUSTE_BENS_IMOVEIS_AMORTIZACAO_DIMINUTIVO("Ajuste de bens imóveis - Amortização - Diminutivo", TipoOperacaoBensImoveisDTO.AJUSTE_BENS_IMOVEIS_AMORTIZACAO_DIMINUTIVO),
    REDUCAO_VALOR_RECUPERAVEL_BENS_IMOVEIS("Redução ao valor recuperável de bens imóveis", TipoOperacaoBensImoveisDTO.REDUCAO_VALOR_RECUPERAVEL_BENS_IMOVEIS),
    APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_DEPRECIACAO("Apuração do valor líquido contábil de bens imóveis - Depreciação", TipoOperacaoBensImoveisDTO.APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_DEPRECIACAO),
    APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_EXAUSTAO("Apuração do valor líquido contábil de bens imóveis - Exaustão", TipoOperacaoBensImoveisDTO.APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_EXAUSTAO),
    APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_AMORTIZACAO("Apuração do valor líquido contábil de bens imóveis - Amortização", TipoOperacaoBensImoveisDTO.APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_AMORTIZACAO),
    APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_REDUCAO_VALOR_RECUPERAVEL("Apuração do valor líquido contábil de bens imóveis - Redução ao valor recuperável", TipoOperacaoBensImoveisDTO.APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_REDUCAO_VALOR_RECUPERAVEL),
    AQUISICAO_BENS_IMOVEIS("Aquisição de bens imóveis", TipoOperacaoBensImoveisDTO.AQUISICAO_BENS_IMOVEIS),
    DESINCORPORACAO_BENS_IMOVEIS("Desincorporação de bens imóveis", TipoOperacaoBensImoveisDTO.DESINCORPORACAO_BENS_IMOVEIS),
    EXAUSTAO_BENS_IMOVEIS("Exaustão de bens imóveis", TipoOperacaoBensImoveisDTO.EXAUSTAO_BENS_IMOVEIS),
    GANHOS_ALIENACAO_BENS_IMOVEIS("Ganhos com alienação de bens Imóveis", TipoOperacaoBensImoveisDTO.GANHOS_ALIENACAO_BENS_IMOVEIS),
    INCORPORACAO_BENS_IMOVEIS("Incorporação de bens imóveis", TipoOperacaoBensImoveisDTO.INCORPORACAO_BENS_IMOVEIS),
    PERDAS_ALIENACAO_BENS_IMOVEIS("Perdas com alienação de bens Imóveis", TipoOperacaoBensImoveisDTO.PERDAS_ALIENACAO_BENS_IMOVEIS),
    REAVALIACAO_BENS_IMOVEIS_AUMENTATIVA("Reavaliação de bens Imóveis aumentativa", TipoOperacaoBensImoveisDTO.REAVALIACAO_BENS_IMOVEIS_AUMENTATIVA),
    REAVALIACAO_BENS_IMOVEIS_DIMINUTIVA("Reavaliação de bens Imóveis diminutiva", TipoOperacaoBensImoveisDTO.REAVALIACAO_BENS_IMOVEIS_DIMINUTIVA),
    TRANSFERENCIA_BENS_IMOVEIS_RECEBIDA("Transferência de bens imóveis recebida", TipoOperacaoBensImoveisDTO.TRANSFERENCIA_BENS_IMOVEIS_RECEBIDA),
    TRANSFERENCIA_BENS_IMOVEIS_CONCEDIDA("Transferência de bens imóveis concedida", TipoOperacaoBensImoveisDTO.TRANSFERENCIA_BENS_IMOVEIS_CONCEDIDA),
    TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_CONCEDIDA("Transferência de bens imóveis depreciação concedida", TipoOperacaoBensImoveisDTO.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_CONCEDIDA),
    TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_CONCEDIDA("Transferência de bens imóveis amortização concedida", TipoOperacaoBensImoveisDTO.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_CONCEDIDA),
    TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_RECEBIDA("Transferência de bens imóveis depreciação recebida", TipoOperacaoBensImoveisDTO.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_RECEBIDA),
    TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_RECEBIDA("Transferência de bens imóveis amortização recebida", TipoOperacaoBensImoveisDTO.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_RECEBIDA);

    private String descricao;
    private TipoOperacaoBensImoveisDTO toDto;

    TipoOperacaoBensImoveis(String descricao, TipoOperacaoBensImoveisDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoOperacaoBensImoveisDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
