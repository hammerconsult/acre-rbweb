package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.TipoExibicaoDTO;

public enum TipoExibicao implements EnumComDescricao {
    CONTA_DE_DESTINACAO("Conta de Destinação de Recurso", TipoExibicaoDTO.CONTA_DE_DESTINACAO),
    FONTE_DE_RECURSO("Fonte de Recursos", TipoExibicaoDTO.FONTE_DE_RECURSO);

    private String descricao;
    private TipoExibicaoDTO toDto;

    TipoExibicao(String descricao, TipoExibicaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoExibicaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
