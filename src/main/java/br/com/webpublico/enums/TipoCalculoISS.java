/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.tributario.TipoCalculoISSDTO;

public enum TipoCalculoISS implements EnumComDescricao {
    ESTIMADO("Estimado", TipoCalculoISSDTO.ESTIMADO),
    MENSAL("Mensal", TipoCalculoISSDTO.MENSAL),
    FIXO("Fixo", TipoCalculoISSDTO.FIXO);
    private String descricao;
    private TipoCalculoISSDTO toDto;

    TipoCalculoISS(String descricao, TipoCalculoISSDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoCalculoISSDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
