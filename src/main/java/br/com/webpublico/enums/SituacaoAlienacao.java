package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoAlienacaoDTO;

/**
 * Created by mga on 29/01/2018.
 */
public enum SituacaoAlienacao {
    EM_ELABORACAO("Em Elaboração"),
    AGUARDANDO_APROVACAO("Aguardando Aprovação"),
    AGUARDANDO_AVALIACAO("Aguardando Avaliação"),
    AVALIADA("Avaliada"),
    APROVADA("Aprovada"),
    ALIENADO("Alienada"),
    FINALIZADA("Finalizada"),
    REJEITADA("Rejeitada");
    private String descricao;
    private SituacaoAlienacaoDTO toDto;

    SituacaoAlienacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoAlienacaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
