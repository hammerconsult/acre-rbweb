/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.webreportdto.dto.tributario.TipoMultaFiscalizacaoDTO;
import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author fabio
 */
@GrupoDiagrama(nome = "Fiscalizacao")
public enum TipoMultaFiscalizacao implements EnumComDescricao {
    PERCENTUAL("Percentual",TipoMultaFiscalizacaoDTO.PERCENTUAL),
    FIXO("Fixo", TipoMultaFiscalizacaoDTO.FIXO);
    private String descricao;
    private TipoMultaFiscalizacaoDTO toDto;

    TipoMultaFiscalizacao(String descricao, TipoMultaFiscalizacaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoMultaFiscalizacaoDTO getToDto() {
        return toDto;
    }
}
