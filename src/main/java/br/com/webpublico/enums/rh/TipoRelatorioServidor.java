package br.com.webpublico.enums.rh;

import br.com.webpublico.webreportdto.dto.rh.TipoRelatorioServidorDTO;

public enum TipoRelatorioServidor {

    NOMEADOS("Nomeados", TipoRelatorioServidorDTO.NOMEADOS),
    EXONERADO("Exonerados", TipoRelatorioServidorDTO.EXONERADO);

    private String descricao;
    private TipoRelatorioServidorDTO toDto;

    TipoRelatorioServidor(String descricao, TipoRelatorioServidorDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoRelatorioServidorDTO getToDto() {
        return toDto;
    }
}
