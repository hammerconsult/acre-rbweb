package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.ModalidadeEmendaDTO;

public enum ModalidadeEmenda {
    DIRETA(1, "Direta", ModalidadeEmendaDTO.DIRETA),
    INDIRETA(2, "Indireta", ModalidadeEmendaDTO.INDIRETA);

    private Integer codigo;
    private String descricao;
    private ModalidadeEmendaDTO toDto;

    ModalidadeEmenda(Integer codigo, String descricao, ModalidadeEmendaDTO toDto) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public ModalidadeEmendaDTO getToDto() {
        return toDto;
    }

    public boolean isIndireta() {
        return INDIRETA.equals(this);
    }

    public boolean isDireta() {
        return DIRETA.equals(this);
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
