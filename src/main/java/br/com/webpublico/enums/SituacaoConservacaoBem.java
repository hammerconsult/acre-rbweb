package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.SituacaoConservacaoBemDTO;

public enum SituacaoConservacaoBem implements EnumComDescricao {
    USO_NORMAL("Uso Normal", SituacaoConservacaoBemDTO.USO_NORMAL),
    OCIOSO("Ocioso", SituacaoConservacaoBemDTO.OCIOSO),
    RECUPERAVEL("Recuperável", SituacaoConservacaoBemDTO.RECUPERAVEL),
    ANTIECONOMICO("Antieconômico", SituacaoConservacaoBemDTO.ANTIECONOMICO),
    IRRECUPERAVEL("Irrecuperável", SituacaoConservacaoBemDTO.IRRECUPERAVEL),
    OBSOLETO("Obsoleto", SituacaoConservacaoBemDTO.OBSOLETO);
    private final String descricao;
    private SituacaoConservacaoBemDTO toDto;

    SituacaoConservacaoBem(String descricao, SituacaoConservacaoBemDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public SituacaoConservacaoBemDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
