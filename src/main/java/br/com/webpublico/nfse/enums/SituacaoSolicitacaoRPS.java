package br.com.webpublico.nfse.enums;

import br.com.webpublico.nfse.domain.dtos.enums.SituacaoSolicitacaoRPSNfseDTO;

public enum SituacaoSolicitacaoRPS {
    AGUARDANDO("Aguardando"),
    DEFERIDA("Deferida"),
    INDEFERIDA("Indeferida");

    private String descricao;

    SituacaoSolicitacaoRPS(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoSolicitacaoRPSNfseDTO toSituacaoDTO() {
        return toSituacaoDTO(this);
    }

    public SituacaoSolicitacaoRPSNfseDTO toSituacaoDTO(SituacaoSolicitacaoRPS situacao) {
        if (situacao != null) {
            switch (situacao) {
                case AGUARDANDO:
                    return SituacaoSolicitacaoRPSNfseDTO.AGUARDANDO;
                case DEFERIDA:
                    return SituacaoSolicitacaoRPSNfseDTO.DEFERIDA;
                case INDEFERIDA:
                    return SituacaoSolicitacaoRPSNfseDTO.INDEFERIDA;
            }
        }
        return null;
    }
}
