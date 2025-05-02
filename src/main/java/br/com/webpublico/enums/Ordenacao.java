package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.comum.OrdenacaoDTO;

/**
 * Created with IntelliJ IDEA.
 * User: FABIO
 * Date: 18/08/15
 * Time: 10:33
 * To change this template use File | Settings | File Templates.
 */
public enum Ordenacao {

    CRESCENTE("Crescente", OrdenacaoDTO.CRESCENTE),
    DECRESCENTE("Decrescente", OrdenacaoDTO.DECRESCENTE);

    private String descricao;
    private OrdenacaoDTO toDto;

    Ordenacao(String descricao, OrdenacaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public OrdenacaoDTO getToDto() {
        return toDto;
    }
}
