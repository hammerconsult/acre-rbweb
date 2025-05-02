/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.SituacaoReceitaExtraDTO;

/**
 * @author major
 */
public enum SituacaoReceitaExtra {

    ABERTO("Aberta", SituacaoReceitaExtraDTO.ABERTO),
    CANCELADO("Cancelada (Migração)", SituacaoReceitaExtraDTO.CANCELADO),
    EFETUADO("Efetuada", SituacaoReceitaExtraDTO.EFETUADO),
    ESTORNADA("Estornada", SituacaoReceitaExtraDTO.ESTORNADA);

    private String descricao;
    private SituacaoReceitaExtraDTO toDto;

    SituacaoReceitaExtra(String descricao, SituacaoReceitaExtraDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoReceitaExtraDTO getToDto() {
        return toDto;
    }
}
