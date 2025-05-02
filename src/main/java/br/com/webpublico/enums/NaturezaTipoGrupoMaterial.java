/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.NaturezaTipoGrupoMaterialDTO;

/**
 * @author Edi
 */
public enum NaturezaTipoGrupoMaterial {

    ORIGINAL("A - Original", NaturezaTipoGrupoMaterialDTO.ORIGINAL),
    AJUSTE_PERDA("B - Ajuste de Perda", NaturezaTipoGrupoMaterialDTO.AJUSTE_PERDA),
    REDUCAO("C - Redução", NaturezaTipoGrupoMaterialDTO.REDUCAO),
    VPD("D - VPD", NaturezaTipoGrupoMaterialDTO.VPD),
    VPA("E - VPA", NaturezaTipoGrupoMaterialDTO.VPA);
    private String descricao;
    private NaturezaTipoGrupoMaterialDTO toDto;

    NaturezaTipoGrupoMaterial(String descricao, NaturezaTipoGrupoMaterialDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public NaturezaTipoGrupoMaterialDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
