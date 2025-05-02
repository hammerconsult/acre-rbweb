package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.OperacaoInvestimentoDTO;

/**
 * Created by mateus on 18/10/17.
 */
public enum OperacaoInvestimento {

    AJUSTE_DEMAIS_INVESTIMENTOS_PERMANENTES_AUMENTATIVO("Ajuste em Demais Investimentos Permanentes – Aumentativo – Empresa Pública", OperacaoInvestimentoDTO.AJUSTE_DEMAIS_INVESTIMENTOS_PERMANENTES_AUMENTATIVO),
    AJUSTE_DEMAIS_INVESTIMENTOS_PERMANENTES_DIMINUTIVO("Ajuste em Demais Investimentos Permanentes – Diminutivo – Empresa Pública", OperacaoInvestimentoDTO.AJUSTE_DEMAIS_INVESTIMENTOS_PERMANENTES_DIMINUTIVO);

    private String descricao;
    private OperacaoInvestimentoDTO toDto;

    OperacaoInvestimento(String descricao, OperacaoInvestimentoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public OperacaoInvestimentoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
