package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.ApresentacaoDespesaFolhaEncargosDTO;
import br.com.webpublico.webreportdto.dto.contabil.TipoDespesaFolhaEncargosDTO;

public enum ApresentacaoDespesaFolhaEncargos implements EnumComDescricao {

    CONSOLIDADO("Consolidado", ApresentacaoDespesaFolhaEncargosDTO.CONSOLIDADO),
    FONTE_DE_RECURSOS("Fonte de Recursos", ApresentacaoDespesaFolhaEncargosDTO.FONTE_DE_RECURSOS),
    UNIDADE("Unidade", ApresentacaoDespesaFolhaEncargosDTO.UNIDADE),
    UNIDADE_E_FONTE_DE_RECURSOS("Unidade e Fonte de Recursos", ApresentacaoDespesaFolhaEncargosDTO.UNIDADE_E_FONTE_DE_RECURSOS);

    private String descricao;
    private ApresentacaoDespesaFolhaEncargosDTO toDto;

    ApresentacaoDespesaFolhaEncargos(String descricao, ApresentacaoDespesaFolhaEncargosDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public ApresentacaoDespesaFolhaEncargosDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
