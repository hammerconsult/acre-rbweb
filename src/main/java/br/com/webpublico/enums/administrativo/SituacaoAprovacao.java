package br.com.webpublico.enums.administrativo;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoAprovacaoDTO;

public enum SituacaoAprovacao {

    APROVADO("Aprovado", SituacaoAprovacaoDTO.APROVADO),
    REJEITADO("Rejeitado",SituacaoAprovacaoDTO.REJEITADO);

    private String descricao;
    private SituacaoAprovacaoDTO toDto;

    SituacaoAprovacao(String descricao, SituacaoAprovacaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoAprovacaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
