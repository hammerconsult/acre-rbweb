/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.rh.TipoCalculoFPDTO;

/**
 * @author peixe
 */
public enum TipoCalculoFP {

    NORMAL("Normal", TipoCalculoFPDTO.NORMAL),
    RETROATIVO("Retroativo", TipoCalculoFPDTO.RETROATIVO);

    private String descricao;
    private TipoCalculoFPDTO toDto;

    TipoCalculoFP(String descricao, TipoCalculoFPDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoCalculoFPDTO getToDto() {
        return toDto;
    }
}
