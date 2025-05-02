package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.OperacaoPatrimonioLiquidoDTO;

/**
 * Created by mga on 18/10/2017.
 */
public enum OperacaoPatrimonioLiquido implements EnumComDescricao {
    AJUSTE_CAPITAL_SOCIAL_SUBSCRITO_DIMINUTIVO("Ajuste em Capital Social Subscrito – Diminutivo – Empresa Pública", OperacaoPatrimonioLiquidoDTO.AJUSTE_CAPITAL_SOCIAL_SUBSCRITO_DIMINUTIVO),
    AJUSTE_CAPITAL_SOCIAL_SUBSCRITO_AUMENTATIVO("Ajuste em Capital Social Subscrito – Aumentativo– Empresa Pública", OperacaoPatrimonioLiquidoDTO.AJUSTE_CAPITAL_SOCIAL_SUBSCRITO_AUMENTATIVO),
    AJUSTE_OUTRAS_RESERVAS_REAVALIACAO_DIMINUTIVO("Ajuste em Outras Reservas de Reavaliação – Diminutivo – Empresa Pública", OperacaoPatrimonioLiquidoDTO.AJUSTE_OUTRAS_RESERVAS_REAVALIACAO_DIMINUTIVO),
    AJUSTE_OUTRAS_RESERVAS_REAVALIACAO_AUMENTATIVO("Ajuste em Outras Reservas de Reavaliação – Aumentativo – Empresa Pública", OperacaoPatrimonioLiquidoDTO.AJUSTE_OUTRAS_RESERVAS_REAVALIACAO_AUMENTATIVO);
    private String descricao;
    private OperacaoPatrimonioLiquidoDTO toDTO;

    OperacaoPatrimonioLiquido(String descricao, OperacaoPatrimonioLiquidoDTO toDTO) {
        this.descricao = descricao;
        this.toDTO = toDTO;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public OperacaoPatrimonioLiquidoDTO getToDTO() {
        return toDTO;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
