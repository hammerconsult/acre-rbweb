/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.SituacaoContaDTO;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroEconomico")
public enum SituacaoConta implements EnumComDescricao {

    ATIVO("Ativa", SituacaoContaDTO.ATIVO),
    INATIVO("Inativa", SituacaoContaDTO.INATIVO),
    BLOQUEADA("Bloqueada", SituacaoContaDTO.BLOQUEADA);

    private String descricao;
    private SituacaoContaDTO toDto;

    SituacaoConta(String descricao, SituacaoContaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public SituacaoContaDTO getToDto() {
        return toDto;
    }

    public boolean isAtivo() {
        return ATIVO.equals(this);
    }

    @Override
    public String toString() {
        return descricao.toString();
    }

}
