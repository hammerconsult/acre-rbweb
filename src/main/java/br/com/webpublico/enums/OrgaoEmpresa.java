/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.rh.OrgaoEmpresaDTO;

/**
 * @author Leonardo
 */
public enum OrgaoEmpresa {

    ORGAO_PUBLICO("Orgão Público", OrgaoEmpresaDTO.ORGAO_PUBLICO),
    EMPRESA_PRIVADA("Empresa Privada", OrgaoEmpresaDTO.EMPRESA_PRIVADA),
    CONTRIBUICAO_INDIVIDUAL("Contribuição Individual", OrgaoEmpresaDTO.CONTRIBUICAO_INDIVIDUAL);

    private String descricao;
    private OrgaoEmpresaDTO dto;

    OrgaoEmpresa(String descricao, OrgaoEmpresaDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public String getDescricao() {
        return descricao;
    }

    public OrgaoEmpresaDTO getDto() {
        return dto;
    }

    @Override
    public String toString() {
        return descricao;
    }


}
