/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.SubSistemaDTO;

/**
 * @author Renato Romanini
 */
public enum SubSistema implements EnumComDescricao {
    ORCAMENTARIO("Orçamentário", SubSistemaDTO.ORCAMENTARIO),
    FINANCEIRO("Financeiro", SubSistemaDTO.FINANCEIRO),
    PERMANENTE("Permanente", SubSistemaDTO.PERMANENTE),
    COMPENSADO("Compensado", SubSistemaDTO.COMPENSADO);
    private String descricao;
    private SubSistemaDTO toDto;

    SubSistema(String descricao, SubSistemaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public SubSistemaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
