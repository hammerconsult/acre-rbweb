/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.CategoriaOrcamentariaDTO;

/**
 * @author venon
 */
public enum CategoriaOrcamentaria implements EnumComDescricao {

    NORMAL("Do Exercício", CategoriaOrcamentariaDTO.NORMAL),
    RESTO("Resto a Pagar", CategoriaOrcamentariaDTO.RESTO);
    private String descricao;
    private CategoriaOrcamentariaDTO toDto;

    CategoriaOrcamentaria(String descricao, CategoriaOrcamentariaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public boolean isNormal() {
        return NORMAL.equals(this);
    }

    public boolean isResto() {
        return RESTO.equals(this);
    }

    public CategoriaOrcamentariaDTO getToDto() {
        return toDto;
    }
}
