/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoFonteRecursoDTO;

/**
 * @author Renato
 */
public enum TipoFonteRecurso {

    NAO_APLICAVEL("Não aplicável", TipoFonteRecursoDTO.NAO_APLICAVEL),
    CONVENIO_RECEITA("Convênio de Receita", TipoFonteRecursoDTO.CONVENIO_RECEITA),
    OPERACAO_CREDITO("Operação de Crédito", TipoFonteRecursoDTO.OPERACAO_CREDITO),
    RETENCAO("Retenção", TipoFonteRecursoDTO.RETENCAO);

    private String descricao;
    private TipoFonteRecursoDTO toDto;

    TipoFonteRecurso(String descricao, TipoFonteRecursoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoFonteRecursoDTO getToDto() {
        return toDto;
    }


    @Override
    public String toString() {
        return descricao;
    }
}
