/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.TipoAjusteDTO;

/**
 * @author Claudio
 */
public enum TipoAjuste implements EnumComDescricao {

    AUMENTATIVO("Aumentativo", TipoAjusteDTO.AUMENTATIVO),
    DIMINUTIVO("Diminutivo", TipoAjusteDTO.DIMINUTIVO);

    private String descricao;
    private TipoAjusteDTO toDto;

    TipoAjuste(String descricao, TipoAjusteDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoAjusteDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
