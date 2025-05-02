package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.TipoDespesaFolhaEncargosDTO;

public enum TipoDespesaFolhaEncargos implements EnumComDescricao {

    ENCARGOS("Encargos", TipoDespesaFolhaEncargosDTO.ENCARGOS),
    PESSOAL("Pessoal", TipoDespesaFolhaEncargosDTO.PESSOAL);

    private String descricao;
    private TipoDespesaFolhaEncargosDTO toDto;

    TipoDespesaFolhaEncargos(String descricao, TipoDespesaFolhaEncargosDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoDespesaFolhaEncargosDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
