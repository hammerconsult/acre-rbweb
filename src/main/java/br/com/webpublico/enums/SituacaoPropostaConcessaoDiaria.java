/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.SituacaoPropostaConcessaoDiariaDTO;

/**
 * @author Usuario
 */
public enum SituacaoPropostaConcessaoDiaria {

    A_COMPROVAR("A Comprovar", SituacaoPropostaConcessaoDiariaDTO.A_COMPROVAR),
    COMPROVADO("Comprovada", SituacaoPropostaConcessaoDiariaDTO.COMPROVADO);
    private String descricao;
    private SituacaoPropostaConcessaoDiariaDTO toDto;

    SituacaoPropostaConcessaoDiaria(String descricao, SituacaoPropostaConcessaoDiariaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoPropostaConcessaoDiariaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
