/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoContratoDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marinzeck
 */
public enum TipoContrato {
    MATERIAL("Material", TipoContratoDTO.MATERIAL),
    SERVICO("Serviço", TipoContratoDTO.SERVICO),
    OBRAS_ENGENHARIA("Obras/Engenharia", TipoContratoDTO.OBRAS_ENGENHARIA);

    private String descricao;
    private TipoContratoDTO toDto;

    TipoContrato(String descricao, TipoContratoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoContratoDTO getToDto() {
        return toDto;
    }

    public static List<TipoContrato> getTipoContratoQueSeEnquadraCom(TipoSolicitacao tipoSolicitacao){
        List<TipoContrato> retorno = new ArrayList<>();
        switch (tipoSolicitacao) {
            case COMPRA_SERVICO:
                retorno.add(TipoContrato.MATERIAL);
                retorno.add(TipoContrato.SERVICO);
                break;
            case  OBRA_SERVICO_DE_ENGENHARIA:
                retorno.add(TipoContrato.OBRAS_ENGENHARIA);
                break;
        }
        return retorno;
    }
}
