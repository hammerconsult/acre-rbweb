package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.rh.TipoExperienciaExtraCurricularDTO;

/**
 * Created by mga on 01/06/2017.
 */
public enum TipoExperienciaExtraCurricular implements EnumComDescricao {
    COMISSAO("Comissão", TipoExperienciaExtraCurricularDTO.COMISSAO),
    CURSO("Curso", TipoExperienciaExtraCurricularDTO.CURSO);
    private String descricao;
    private TipoExperienciaExtraCurricularDTO toDto;

    TipoExperienciaExtraCurricular(String descricao, TipoExperienciaExtraCurricularDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoExperienciaExtraCurricularDTO getToDto() {
        return toDto;
    }
}
