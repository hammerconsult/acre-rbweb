/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.OrigemSuplementacaoORCDTO;

/**
 * @author reidocrime
 */
@GrupoDiagrama(nome = "Orcamentario")
public enum OrigemSuplementacaoORC implements EnumComDescricao {

    ANULACAO("Anulação Parcial ou Total de Dotação", OrigemSuplementacaoORCDTO.ANULACAO),
    EXCESSO("Excesso de Arrecadação", OrigemSuplementacaoORCDTO.EXCESSO),
    SUPERAVIT("Superávit Financeiro", OrigemSuplementacaoORCDTO.SUPERAVIT),
    RESERVA_CONTINGENCIA("Reserva de Contingência", OrigemSuplementacaoORCDTO.RESERVA_CONTINGENCIA),
    OPERACAO_CREDITO("Operação de Crédito", OrigemSuplementacaoORCDTO.OPERACAO_CREDITO);

    private String descricao;
    private OrigemSuplementacaoORCDTO toDto;

    OrigemSuplementacaoORC(String descricao, OrigemSuplementacaoORCDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public OrigemSuplementacaoORCDTO getToDto() {
        return toDto;
    }
}
