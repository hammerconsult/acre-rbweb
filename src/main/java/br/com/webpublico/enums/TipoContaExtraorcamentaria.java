/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.TipoContaExtraorcamentariaDTO;

/**
 *
 * @author Edi
 */
public enum TipoContaExtraorcamentaria implements EnumComDescricao {

    DEPOSITOS_DIVERSOS("Depósitos Diversos", TipoContaExtraorcamentariaDTO.DEPOSITOS_DIVERSOS),
    DEPOSITOS_NAO_JUDICIAIS("Depósitos não Judiciais", TipoContaExtraorcamentariaDTO.DEPOSITOS_NAO_JUDICIAIS),
    DEPOSITOS_JUDICIAIS ("Depósitos Judiciais", TipoContaExtraorcamentariaDTO.DEPOSITOS_JUDICIAIS ),
    DEPOSITOS_GARANTIAS ("Depósitos Garantias", TipoContaExtraorcamentariaDTO.DEPOSITOS_GARANTIAS ),
    DEPOSITOS_CONSIGNACOES("Depósitos Consignações", TipoContaExtraorcamentariaDTO.DEPOSITOS_CONSIGNACOES);

    private String descricao;
    private TipoContaExtraorcamentariaDTO toDto;

    TipoContaExtraorcamentaria(String descricao, TipoContaExtraorcamentariaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public TipoContaExtraorcamentariaDTO getToDto() {
        return toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
