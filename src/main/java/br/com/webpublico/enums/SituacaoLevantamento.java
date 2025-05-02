package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoLevantamentoDTO;

public enum SituacaoLevantamento {
    EM_ELABORACAO("Em Elaboração", SituacaoLevantamentoDTO.EM_ELABORACAO),
    FINALIZADO("Finalizado", SituacaoLevantamentoDTO.FINALIZADO),
    EFETIVADO("Efetivado", SituacaoLevantamentoDTO.EFETIVADO);
    private String descricao;
    private SituacaoLevantamentoDTO toDto;

    SituacaoLevantamento(String descricao, SituacaoLevantamentoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoLevantamentoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
