package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.SituacaoMovimentoMaterialDTO;

public enum SituacaoMovimentoMaterial implements EnumComDescricao {

    EM_ELABORACAO("Em Elaboração", SituacaoMovimentoMaterialDTO.EM_ELABORACAO),
    CONCLUIDA("Concluída", SituacaoMovimentoMaterialDTO.CONCLUIDA);
    private String descricao;
    private SituacaoMovimentoMaterialDTO toDto;

    SituacaoMovimentoMaterial(String descricao, SituacaoMovimentoMaterialDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public SituacaoMovimentoMaterialDTO getToDto() {
        return toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public boolean isEmElaboracao(){
        return SituacaoMovimentoMaterial.EM_ELABORACAO.equals(this);
    }

    public boolean isConcluida(){
        return SituacaoMovimentoMaterial.CONCLUIDA.equals(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
