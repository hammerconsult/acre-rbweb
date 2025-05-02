/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.SituacaoBorderoDTO;

/**
 * @author fabio
 */
public enum SituacaoBordero {

    ABERTO("Aberta", SituacaoBorderoDTO.ABERTO),
    DEFERIDO("Deferida", SituacaoBorderoDTO.DEFERIDO),
    INDEFERIDO("Indeferida", SituacaoBorderoDTO.INDEFERIDO),
    AGUARDANDO_ENVIO("Aguardando Envio", SituacaoBorderoDTO.AGUARDANDO_ENVIO),
    AGUARDANDO_BAIXA("Aguardando Baixa", SituacaoBorderoDTO.AGUARDANDO_BAIXA),
    EFETUADO("Efetuada", SituacaoBorderoDTO.EFETUADO),
    PAGO("Paga", SituacaoBorderoDTO.PAGO),
    PAGO_COM_RESTRICOES("Pago com restrições", SituacaoBorderoDTO.PAGO_COM_RESTRICOES);

    private String descricao;
    private SituacaoBorderoDTO toDto;

    SituacaoBordero(String descricao, SituacaoBorderoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoBorderoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }


}
