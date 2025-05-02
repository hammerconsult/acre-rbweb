/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.webreportdto.dto.administrativo.TipoAprovacaoMaterialDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author renato
 */
@GrupoDiagrama(nome = "Materiais")
public enum TipoAprovacaoMaterial {
    APROVADO_TOTAL("Aprovado Total", TipoAprovacaoMaterialDTO.APROVADO_TOTAL),
    APROVADO_PARCIAL("Aprovado Parcial", TipoAprovacaoMaterialDTO.APROVADO_PARCIAL),
    REJEITADO("Rejeitado", TipoAprovacaoMaterialDTO.REJEITADO);
    private String descricao;
    private TipoAprovacaoMaterialDTO toDto;

    TipoAprovacaoMaterial(String descricao, TipoAprovacaoMaterialDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoAprovacaoMaterialDTO getToDto() {
        return toDto;
    }

    public static List<TipoAprovacaoMaterial> getTiposAprovados(){
        List<TipoAprovacaoMaterial> tipos = Lists.newArrayList();
        tipos.add(APROVADO_PARCIAL);
        tipos.add(APROVADO_TOTAL);
        return tipos;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
