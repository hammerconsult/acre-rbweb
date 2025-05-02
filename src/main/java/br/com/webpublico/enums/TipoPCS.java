/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.rh.TipoPCSDTO;

/**
 * @author peixe
 */
public enum TipoPCS {

    QUADRO_EFETIVO("Quadro Efetivo", TipoPCSDTO.QUADRO_EFETIVO),
    QUADRO_TEMPORARIO("Quadro Temporário", TipoPCSDTO.QUADRO_TEMPORARIO),
    FUNCAO_GRATIFICADA("Função Gratificada", TipoPCSDTO.FUNCAO_GRATIFICADA),
    FUNCAO_GRATIFICADA_COORDENACAO("Função Gratificada Coordenação", TipoPCSDTO.FUNCAO_GRATIFICADA),
    CARGO_EM_COMISSAO("Cargo em Comissão", TipoPCSDTO.CARGO_EM_COMISSAO),
    ACESSO_SUBSIDIO("Acesso Subsídio", null);

    private String descricao;
    private TipoPCSDTO toDto;

    TipoPCS(String descricao, TipoPCSDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoPCSDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static TipoPCS[] getTiposCarreira() {
        return new TipoPCS[]{QUADRO_EFETIVO, QUADRO_TEMPORARIO};
    }

    public static TipoPCS[] getTiposQuadroEfetivoCargoComissao() {
        return new TipoPCS[]{QUADRO_EFETIVO, CARGO_EM_COMISSAO, FUNCAO_GRATIFICADA};
    }

    public static TipoPCS[] getTiposComissao() {
        return new TipoPCS[]{FUNCAO_GRATIFICADA, CARGO_EM_COMISSAO};
    }
}
