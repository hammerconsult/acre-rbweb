/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.comum.TipoHierarquiaOrganizacionalDTO;

/**
 * @author reidocrime
 */
public enum TipoHierarquiaOrganizacional {

    ADMINISTRATIVA("Administrativa", TipoHierarquiaOrganizacionalDTO.ADMINISTRATIVA),
    ORCAMENTARIA("Orçamentária", TipoHierarquiaOrganizacionalDTO.ORCAMENTARIA);
    private String descricao;
    private TipoHierarquiaOrganizacionalDTO toDto;

    private TipoHierarquiaOrganizacional(String descricao, TipoHierarquiaOrganizacionalDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoHierarquiaOrganizacionalDTO getToDto() {
        return toDto;
    }

    public boolean isOrcamentaria() {
        return TipoHierarquiaOrganizacional.ORCAMENTARIA.equals(this);
    }

    public boolean isAdministrativa() {
        return TipoHierarquiaOrganizacional.ADMINISTRATIVA.equals(this);
    }
}
