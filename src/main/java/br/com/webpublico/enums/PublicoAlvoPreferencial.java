package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.PublicoAlvoPreferencialDTO;

public enum PublicoAlvoPreferencial implements EnumComDescricao {

    CRIANCA_ZERO_TRES("Crianças (0 a 3 anos)", PublicoAlvoPreferencialDTO.CRIANCA_ZERO_TRES),
    CRIANCA_QUATRO_CINCO("Crianças (4 a 5 anos)", PublicoAlvoPreferencialDTO.CRIANCA_QUATRO_CINCO),
    CRIANCA_SEIS_QUATORZE("Crianças (6 a 14 anos)", PublicoAlvoPreferencialDTO.CRIANCA_SEIS_QUATORZE),
    JOVEM("Jovens (15 a 17 anos)", PublicoAlvoPreferencialDTO.JOVEM),
    ADULTO("Adultos (18 a 59 anos)", PublicoAlvoPreferencialDTO.ADULTO),
    IDOSO("Idosos (com mais de 59 anos)", PublicoAlvoPreferencialDTO.IDOSO),
    TODOS("Todos", PublicoAlvoPreferencialDTO.TODOS);
    private String descricao;

    private PublicoAlvoPreferencialDTO dto;

    PublicoAlvoPreferencial(String descricao, PublicoAlvoPreferencialDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public PublicoAlvoPreferencialDTO getDto() {
        return dto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
