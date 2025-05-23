/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.tributario.SituacaoAcaoFiscalDTO;

/**
 * @author Claudio
 */
public enum SituacaoAcaoFiscal implements EnumComDescricao {
    PROGRAMADO("Programado", SituacaoAcaoFiscalDTO.PROGRAMADO),
    EXECUTANDO("Executando", SituacaoAcaoFiscalDTO.EXECUTANDO),
    CONCLUIDO("Concluído", SituacaoAcaoFiscalDTO.CONCLUIDO),
    CANCELADO("Cancelado", SituacaoAcaoFiscalDTO.CANCELADO),
    REABERTO("Reaberto", SituacaoAcaoFiscalDTO.REABERTO),
    DESIGNADO("Designado", SituacaoAcaoFiscalDTO.DESIGNADO);
    private String descricao;
    private SituacaoAcaoFiscalDTO toDto;

    SituacaoAcaoFiscal(String descricao, SituacaoAcaoFiscalDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() { return descricao;
    }

    public SituacaoAcaoFiscalDTO getToDto() {
        return toDto;
    }
}
