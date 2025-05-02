package br.com.webpublico.enums.rh;

import br.com.webpublico.webreportdto.dto.rh.TipoRelatorioAtualizacaoServidorDTO;

public enum TipoRelatorioAtualizacaoServidor {
    LISTAGEM_ATUALIZADOS("Listagem de Dados Atualizados", TipoRelatorioAtualizacaoServidorDTO.LISTAGEM_ATUALIZADOS),
    LISTAGEM_NAO_AUTORIZADOS("Listagem de Dados não Atualizados", TipoRelatorioAtualizacaoServidorDTO.LISTAGEM_NAO_ATUALIZADOS);

    private String descricao;
    private TipoRelatorioAtualizacaoServidorDTO toDto;

    TipoRelatorioAtualizacaoServidor(String descricao, TipoRelatorioAtualizacaoServidorDTO toDto) {
        this.toDto = toDto;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoRelatorioAtualizacaoServidorDTO getToDto() {
        return toDto;
    }
}
