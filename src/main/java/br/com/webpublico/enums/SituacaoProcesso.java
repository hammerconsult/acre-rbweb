/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoProcessoDTO;

/**
 * @author Terminal2
 */
public enum SituacaoProcesso {

    GERADO("Gerado", SituacaoProcessoDTO.GERADO),
    EMTRAMITE("Em Trâmite", SituacaoProcessoDTO.EMTRAMITE),
    CANCELADO("Cancelado", SituacaoProcessoDTO.CANCELADO),
    ARQUIVADO("Arquivado", SituacaoProcessoDTO.ARQUIVADO),
    DEFERIDOARQUIVADO("Deferido Arquivado", SituacaoProcessoDTO.DEFERIDOARQUIVADO),
    INDEFERIDOARQUIVADO("Indeferido Arquivado", SituacaoProcessoDTO.INDEFERIDOARQUIVADO),
    INCORPORADO("Incorporado", SituacaoProcessoDTO.INCORPORADO),
    FINALIZADO("Finalizado", SituacaoProcessoDTO.FINALIZADO);
    private String descricao;
    private SituacaoProcessoDTO toDto;

    SituacaoProcesso(String descricao, SituacaoProcessoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoProcessoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
