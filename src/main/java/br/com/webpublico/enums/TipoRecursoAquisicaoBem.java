/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoRecursoAquisicaoBemDTO;
/**
 *
 * @author Arthur Zavadski
 * @author Lucas Cheles
 */
public enum TipoRecursoAquisicaoBem {
    PROPRIO("Próprio", TipoRecursoAquisicaoBemDTO.PROPRIO),
    FUNDO("Fundo", TipoRecursoAquisicaoBemDTO.FUNDO),
    CONVENIO("Convênio", TipoRecursoAquisicaoBemDTO.CONVENIO),
    OUTROS("Outros", TipoRecursoAquisicaoBemDTO.OUTROS);
    private String descricao;
    private TipoRecursoAquisicaoBemDTO toDto;

    TipoRecursoAquisicaoBem(String descricao, TipoRecursoAquisicaoBemDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoRecursoAquisicaoBemDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
