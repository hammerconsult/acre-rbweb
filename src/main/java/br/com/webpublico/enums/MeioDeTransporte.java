/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.MeioDeTransporteDTO;

/**
 * @author reidocrime
 */
public enum MeioDeTransporte implements EnumComDescricao {
    AEREO("Aéreo", MeioDeTransporteDTO.AEREO),
    TERRESTRE("Terrestre", MeioDeTransporteDTO.TERRESTRE),
    OUTROS("Outros", MeioDeTransporteDTO.OUTROS);

    private String descricao;
    private MeioDeTransporteDTO toDto;

    MeioDeTransporte(String descricao, MeioDeTransporteDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public MeioDeTransporteDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
