/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.TipoProvisaoDTO;

/**
 * @author Claudio
 */
public enum TipoProvisao implements EnumComDescricao {

    PROVISAO_BENEFICIOS_CONCEDIDOS("Provisão de Benefícios Concedidos", TipoProvisaoDTO.PROVISAO_BENEFICIOS_CONCEDIDOS),
    PROVISAO_BENEFICIOS_CONCEDER("Provisão de Benefícios a Conceder", TipoProvisaoDTO.PROVISAO_BENEFICIOS_CONCEDER),
    PLANO_AMORTIZACAO("Plano de Amortização", TipoProvisaoDTO.PLANO_AMORTIZACAO),
    PROVISAO_ATUARIAL_AJUSTE("Provisão Atuarial para Ajuste do Plano", TipoProvisaoDTO.PROVISAO_ATUARIAL_AJUSTE);
    private String descricao;
    private TipoProvisaoDTO toDto;

    TipoProvisao(String descricao, TipoProvisaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoProvisaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
