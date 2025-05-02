package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoCessaoDTO;

/**
 * @author Julio
 */
public enum TipoCessao {

    INTERNO("Interna", TipoCessaoDTO.INTERNO),
    EXTERNO("Externa", TipoCessaoDTO.EXTERNO);

    private String descricao;
    private TipoCessaoDTO toDto;

    TipoCessao(String descricao, TipoCessaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoCessaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
