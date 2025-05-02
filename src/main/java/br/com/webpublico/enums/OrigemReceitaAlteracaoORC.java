/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.webreportdto.dto.contabil.OrigemReceitaAlteracaoORCDTO;

/**
 * @author reidocrime
 */
@GrupoDiagrama(nome = "Orcamentario")
public enum OrigemReceitaAlteracaoORC {
    OPERACAO_CREDITO("Operação de Crédito", OrigemReceitaAlteracaoORCDTO.OPERACAO_CREDITO),
    OPERACAO_EXCESSO("Excesso de Arrecadação", OrigemReceitaAlteracaoORCDTO.OPERACAO_EXCESSO);
    private String descricao;
    private OrigemReceitaAlteracaoORCDTO toDto;

    OrigemReceitaAlteracaoORC(String descricao, OrigemReceitaAlteracaoORCDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public OrigemReceitaAlteracaoORCDTO getToDto() {
        return toDto;
    }
}
