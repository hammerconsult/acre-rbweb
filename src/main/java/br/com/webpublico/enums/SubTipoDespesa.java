/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.SubTipoDespesaDTO;

/**
 * @author wiplash
 */
public enum SubTipoDespesa {

    JUROS("Juros", SubTipoDespesaDTO.JUROS),
    NAO_APLICAVEL("Não Aplicável", SubTipoDespesaDTO.NAO_APLICAVEL),
    OUTROS_ENCARGOS("Outros Encargos", SubTipoDespesaDTO.OUTROS_ENCARGOS),
    RGPS("RGPS", SubTipoDespesaDTO.RGPS),
    RPPS("RPPS", SubTipoDespesaDTO.RPPS),
    VALOR_PRINCIPAL("Principal", SubTipoDespesaDTO.VALOR_PRINCIPAL);

    private String descricao;
    private SubTipoDespesaDTO toDto;

    SubTipoDespesa(String descricao, SubTipoDespesaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SubTipoDespesaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
