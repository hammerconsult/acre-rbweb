package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoIRPDTO;

public enum SituacaoIRP {
    EM_ELABORACAO("Em Elaboração", SituacaoIRPDTO.EM_ELABORACAO),
    CONCLUIDA("Concluída", SituacaoIRPDTO.CONCLUIDA),
    AGUARDANDO_APROVACAO("Aguardando Aprovação", SituacaoIRPDTO.AGUARDANDO_APROVACAO),
    APROVADA("Aprovada", SituacaoIRPDTO.APROVADA),
    RECUSADA("Recusada", SituacaoIRPDTO.RECUSADA);
    private String descricao;
    private SituacaoIRPDTO toDto;

    SituacaoIRP(String descricao, SituacaoIRPDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoIRPDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isEmElaboracao() {
        return SituacaoIRP.EM_ELABORACAO.equals(this);
    }

    public boolean isConcluida() {
        return SituacaoIRP.CONCLUIDA.equals(this);
    }

    public boolean isAprovada() {
        return SituacaoIRP.APROVADA.equals(this);
    }

    public boolean isRecusada() {
        return SituacaoIRP.RECUSADA.equals(this);
    }
}
