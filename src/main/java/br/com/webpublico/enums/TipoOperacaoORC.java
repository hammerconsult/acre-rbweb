/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoOperacaoORCDTO;

/**
 * @author venon
 */
public enum TipoOperacaoORC {

    NORMAL("Normal", TipoOperacaoORCDTO.NORMAL),
    ESTORNO("Estorno", TipoOperacaoORCDTO.ESTORNO);

    private String descricao;
    private TipoOperacaoORCDTO toDto;

    TipoOperacaoORC(String descricao, TipoOperacaoORCDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoOperacaoORCDTO getToDto() {
        return toDto;
    }

    public boolean isNormal() {
        return TipoOperacaoORC.NORMAL.equals(this);
    }

    public boolean isEstorno() {
        return TipoOperacaoORC.ESTORNO.equals(this);
    }
}
