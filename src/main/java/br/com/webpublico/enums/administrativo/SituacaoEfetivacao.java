package br.com.webpublico.enums.administrativo;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoEfetivacaoDTO;

public enum SituacaoEfetivacao {

    ACEITA("Aceita", SituacaoEfetivacaoDTO.ACEITA),
    REJEITADA("Rejeitada", SituacaoEfetivacaoDTO.REJEITADA);

    private String descricao;
    private SituacaoEfetivacaoDTO toDto;

    SituacaoEfetivacao(String descricao, SituacaoEfetivacaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoEfetivacaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
