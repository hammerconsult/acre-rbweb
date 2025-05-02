package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoTransferenciaDeBensDTO;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 18/02/14
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public enum TipoTransferenciaDeBens {

    CONCEDIDA("Concedida", TipoTransferenciaDeBensDTO.CONCEDIDA),
    RECEBIDA("Recebida", TipoTransferenciaDeBensDTO.RECEBIDA);

    private String descricao;
    private TipoTransferenciaDeBensDTO toDto;

    TipoTransferenciaDeBens(String descricao, TipoTransferenciaDeBensDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoTransferenciaDeBensDTO getToDto() {
        return toDto;
    }
}
