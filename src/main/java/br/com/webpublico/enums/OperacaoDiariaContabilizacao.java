/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.OperacaoDiariaContabilizacaoDTO;

/**
 * @author juggernaut
 */
public enum OperacaoDiariaContabilizacao implements EnumComDescricao {

    INSCRICAO("Inscrição", OperacaoDiariaContabilizacaoDTO.INSCRICAO),
    APROPRIACAO("Apropriação", OperacaoDiariaContabilizacaoDTO.APROPRIACAO),
    BAIXA("Baixa", OperacaoDiariaContabilizacaoDTO.BAIXA);

    private String descricao;
    private OperacaoDiariaContabilizacaoDTO toDto;

    OperacaoDiariaContabilizacao(String descricao, OperacaoDiariaContabilizacaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public OperacaoDiariaContabilizacaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
