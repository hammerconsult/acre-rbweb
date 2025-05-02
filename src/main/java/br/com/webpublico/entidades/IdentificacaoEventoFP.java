package br.com.webpublico.entidades;

import br.com.webpublico.webreportdto.dto.rh.IdentificacaoEventoFPDTO;

/**
 * @Author peixe on 28/04/2016  15:24.
 */
public enum IdentificacaoEventoFP {

    IMPOSTO_RENDA("Imposto de Renda", IdentificacaoEventoFPDTO.IMPOSTO_RENDA),
    RPPS("RPPS", IdentificacaoEventoFPDTO.RPPS),
    INSS("INSS", IdentificacaoEventoFPDTO.INSS),
    FGTS("FGTS", IdentificacaoEventoFPDTO.FGTS),
    PENSAO_ALIMENTICIA("Pensão Alimentícia", IdentificacaoEventoFPDTO.PENSAO_ALIMENTICIA),
    FUNCAO_GRATIFICADA("Função Gratificada", IdentificacaoEventoFPDTO.FUNCAO_GRATIFICADA),
    FALTA("Falta", IdentificacaoEventoFPDTO.FALTA);


    private String descricao;
    private IdentificacaoEventoFPDTO toDto;

    IdentificacaoEventoFP(String descricao, IdentificacaoEventoFPDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public IdentificacaoEventoFPDTO getToDto() {
        return toDto;
    }
}
