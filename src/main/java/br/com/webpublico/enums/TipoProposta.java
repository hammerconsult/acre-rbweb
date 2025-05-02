/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoPropostaDTO;

/**
 * @author venon
 */
public enum TipoProposta {

    SUPRIMENTO_FUNDO("Suprimento de Fundo", TipoPropostaDTO.SUPRIMENTO_FUNDO),
    CONCESSAO_DIARIA("Diária Civil", TipoPropostaDTO.CONCESSAO_DIARIA),
    CONCESSAO_DIARIACAMPO("Diária de Campo", TipoPropostaDTO.CONCESSAO_DIARIACAMPO),
    COLABORADOR_EVENTUAL("Colaborador Eventual", TipoPropostaDTO.COLABORADOR_EVENTUAL);

    private String descricao;
    private TipoPropostaDTO toDto;

    TipoProposta(String descricao, TipoPropostaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoPropostaDTO getToDto() {
        return toDto;
    }
}
