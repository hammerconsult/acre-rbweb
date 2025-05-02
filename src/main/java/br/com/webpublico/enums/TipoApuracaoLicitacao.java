/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.TipoApuracaoLicitacaoDTO;

/**
 * @author renato
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoApuracaoLicitacao implements EnumComDescricao {

    ITEM("Por Item", TipoApuracaoLicitacaoDTO.ITEM),
    LOTE("Por Lote", TipoApuracaoLicitacaoDTO.LOTE);
    private String descricao;
    private TipoApuracaoLicitacaoDTO toDto;

    TipoApuracaoLicitacao(String descricao, TipoApuracaoLicitacaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoApuracaoLicitacaoDTO getToDto() {
        return toDto;
    }

    public boolean isPorItem() {
        return ITEM.equals(this);
    }

    public boolean isPorLote() {
        return LOTE.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
