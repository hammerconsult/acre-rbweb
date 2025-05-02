/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.TipoStatusItemPregaoDTO;

/**
 * @author cheles
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoStatusItemPregao implements EnumComDescricao {

    DESERTO("Deserto", TipoStatusItemPregaoDTO.DESERTO),
    EM_ANDAMENTO("Em Andamento", TipoStatusItemPregaoDTO.EM_ANDAMENTO),
    FINALIZADO("Finalizado", TipoStatusItemPregaoDTO.FINALIZADO),
    CANCELADO("Cancelado", TipoStatusItemPregaoDTO.CANCELADO),
    DECLINADO("Declinado", TipoStatusItemPregaoDTO.DECLINADO),
    INEXEQUIVEL("Inexequível", TipoStatusItemPregaoDTO.INEXEQUIVEL),
    PREJUDICADO("Prejudicado", TipoStatusItemPregaoDTO.PREJUDICADO);
    private String descricao;
    private TipoStatusItemPregaoDTO toDto;

    TipoStatusItemPregao(String descricao, TipoStatusItemPregaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoStatusItemPregaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
