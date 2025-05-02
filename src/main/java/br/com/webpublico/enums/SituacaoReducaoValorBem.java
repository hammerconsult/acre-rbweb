package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.SituacaoReducaoValorBemDTO;

public enum SituacaoReducaoValorBem implements EnumComDescricao {

    EM_ELABORACAO("Em Elaboração", SituacaoReducaoValorBemDTO.EM_ELABORACAO),
    EM_ANDAMENTO("Em Andamento" , SituacaoReducaoValorBemDTO.EM_ANDAMENTO),
    ESTORNO_EM_ANDAMENTO("Estorno em Andamento", SituacaoReducaoValorBemDTO.ESTORNO_EM_ANDAMENTO),
    ESTORNADA("Estornada", SituacaoReducaoValorBemDTO.ESTORNADA),
    CONCLUIDA("Concluída", SituacaoReducaoValorBemDTO.CONCLUIDA);
    private String descricao;
    private SituacaoReducaoValorBemDTO  dto;

    SituacaoReducaoValorBem(String descricao, SituacaoReducaoValorBemDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public SituacaoReducaoValorBemDTO getDto() {
        return dto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
