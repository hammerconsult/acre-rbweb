/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.rh.TipoHoraExtraDTO;

/**
 * @author andre
 */
public enum TipoHoraExtra implements EnumComDescricao {
    HORA_EXTRA_50("Hora Extra 50%", 50, TipoHoraExtraDTO.HORA_EXTRA_50),
    HORA_EXTRA_100("Hora Extra 100%", 100, TipoHoraExtraDTO.HORA_EXTRA_100);
    private String descricao;
    private Integer valor;
    private TipoHoraExtraDTO toDto;

    TipoHoraExtra(String descricao, Integer valor, TipoHoraExtraDTO toDto) {
        this.descricao = descricao;
        this.valor = valor;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public Integer getValor() {
        return valor;
    }

    public TipoHoraExtraDTO getToDto() {
        return toDto;
    }

    public static TipoHoraExtra getTipoHoraExtraPorValor(int valor) {
        for (TipoHoraExtra t : TipoHoraExtra.values()) {
            if (valor == t.valor.intValue()) {
                return t;
            }
        }

        return null;
    }
}
