package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.OpcaoDespesaFolhaEncargosDTO;

public enum OpcaoDespesaFolhaEncargos implements EnumComDescricao {

    EMPENHADO("Empenhado", OpcaoDespesaFolhaEncargosDTO.EMPENHADO),
    LIQUIDADO("Liquidado", OpcaoDespesaFolhaEncargosDTO.LIQUIDADO),
    PAGO("Pago", OpcaoDespesaFolhaEncargosDTO.PAGO);

    private String descricao;
    private OpcaoDespesaFolhaEncargosDTO toDto;

    OpcaoDespesaFolhaEncargos(String descricao, OpcaoDespesaFolhaEncargosDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public OpcaoDespesaFolhaEncargosDTO getToDto() {
        return toDto;
    }
}
