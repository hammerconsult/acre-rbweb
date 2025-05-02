package br.com.webpublico.enums.rh;

import br.com.webpublico.webreportdto.dto.rh.OpcaoGeracaoRelatorioDTO;

public enum OpcaoGeracaoRelatorio {

    GERAL("Geral", OpcaoGeracaoRelatorioDTO.GERAL),
    SECRETARIA("Secretaria", OpcaoGeracaoRelatorioDTO.SECRETARIA);

    private String descricao;
    private OpcaoGeracaoRelatorioDTO toDto;

    OpcaoGeracaoRelatorio(String descricao, OpcaoGeracaoRelatorioDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public OpcaoGeracaoRelatorioDTO getToDto() {
        return toDto;
    }
}
