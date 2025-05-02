/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.TipoViagemDTO;

/**
 * @author reidocrime
 */
public enum TipoViagem implements EnumComDescricao {
    ESTADUAL("Estadual", TipoViagemDTO.ESTADUAL),
    INTERESTADUAL("Interestadual", TipoViagemDTO.INTERESTADUAL),
    INTERNACIONAL("Internacional", TipoViagemDTO.INTERNACIONAL);
    private String descricao;
    private TipoViagemDTO toDto;

    TipoViagem(String descricao, TipoViagemDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoViagemDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
