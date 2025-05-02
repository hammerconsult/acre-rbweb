/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.webreportdto.dto.contabil.TipoEmpresaDTO;

/**
 * @author terminal3
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoEmpresa {
    INDEFINIDO("Indefinido", TipoEmpresaDTO.INDEFINIDO),
    MICRO("Micro-ME", TipoEmpresaDTO.MICRO),
    PEQUENA("Pequena-EPP", TipoEmpresaDTO.PEQUENA),
    NORMAL("Normal", TipoEmpresaDTO.NORMAL),
    GRANDE("Grande", TipoEmpresaDTO.GRANDE);
    private String descricao;
    private TipoEmpresaDTO toDto;

    TipoEmpresa(String descricao, TipoEmpresaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoEmpresaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
