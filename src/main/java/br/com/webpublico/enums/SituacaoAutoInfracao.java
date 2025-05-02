/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.tributario.SituacaoAutoInfracaoDTO;

/**
 * @author Claudio
 */
public enum SituacaoAutoInfracao implements EnumComDescricao {
    GERADO("Gerado", SituacaoAutoInfracaoDTO.GERADO),
    ESTORNADO("Estornado", SituacaoAutoInfracaoDTO.ESTORNADO),
    RETIFICADO("Retificado", SituacaoAutoInfracaoDTO.RETIFICADO),
    CIENCIA("Ciência", SituacaoAutoInfracaoDTO.CIENCIA),
    REVELIA("Revelia", SituacaoAutoInfracaoDTO.REVELIA),
    CANCELADO("Cancelado", SituacaoAutoInfracaoDTO.CANCELADO);
    private String descricao;
    private SituacaoAutoInfracaoDTO toDto;

    SituacaoAutoInfracao(String descricao, SituacaoAutoInfracaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public SituacaoAutoInfracaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }


}
