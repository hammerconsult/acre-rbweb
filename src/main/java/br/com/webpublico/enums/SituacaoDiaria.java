/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.SituacaoDiariaDTO;

/**
 *
 * @author wiplash
 */
public enum SituacaoDiaria {

    ABERTO("Aberta", SituacaoDiariaDTO.ABERTO),
    DEFERIDO("Deferida", SituacaoDiariaDTO.DEFERIDO),
    INDEFERIDO("Indeferida", SituacaoDiariaDTO.INDEFERIDO);

    private String descricao;
    private SituacaoDiariaDTO toDto;

    SituacaoDiaria(String descricao, SituacaoDiariaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoDiariaDTO getToDto() {
        return toDto;
    }

    public boolean isAberto() {
        return ABERTO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
