/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.dto.TipoAlteracaoORCDTO;
import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Claudio
 */
public enum TipoAlteracaoORC implements EnumComDescricao {
    PREVISAO("Previsão", TipoAlteracaoORCDTO.PREVISAO),
    ANULACAO("Anulação", TipoAlteracaoORCDTO.ANULACAO);

    private String descricao;
    private TipoAlteracaoORCDTO toDto;

    TipoAlteracaoORC(String descricao, TipoAlteracaoORCDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoAlteracaoORCDTO getToDto() {
        return toDto;
    }
}
