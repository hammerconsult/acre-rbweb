package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.TipoAlteracaoContratualDTO;

public enum TipoAlteracaoContratual implements EnumComDescricao {
    ADITIVO("Aditivo", TipoAlteracaoContratualDTO.ADITIVO),
    APOSTILAMENTO("Apostilamento", TipoAlteracaoContratualDTO.APOSTILAMENTO);

    private String descricao;
    private TipoAlteracaoContratualDTO dto;

    TipoAlteracaoContratual(String descricao, TipoAlteracaoContratualDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAditivo() {
        return ADITIVO.equals(this);
    }

    public boolean isApostilamento() {
        return APOSTILAMENTO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
