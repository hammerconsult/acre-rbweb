package br.com.webpublico.nfse.domain;

import br.com.webpublico.nfse.domain.dtos.enums.SituacaoAidfeNfseDTO;

public enum SituacaoAidfe {
    AGUARDANDO("Aguardando"),
    DEFERIDA("Deferida"),
    DEFERIDA_PARCIALMENTE("Deferida Parcialmente"),
    INDEFERIDA("Indeferida");

    private String descricao;

    SituacaoAidfe(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoAidfeNfseDTO toSituacaoDTO() {
        return toSituacaoDTO(this);
    }

    public SituacaoAidfeNfseDTO toSituacaoDTO(SituacaoAidfe situacao) {
        if (situacao != null) {
            switch (situacao) {
                case AGUARDANDO:
                    return SituacaoAidfeNfseDTO.AGUARDANDO;
                case DEFERIDA:
                    return SituacaoAidfeNfseDTO.DEFERIDA;
                case DEFERIDA_PARCIALMENTE:
                    return SituacaoAidfeNfseDTO.PARCIALMENTE;
                case INDEFERIDA:
                    return SituacaoAidfeNfseDTO.INDEFERIDA;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
