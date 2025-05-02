/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.EsferaOrcamentariaDTO;

/**
 * @author major
 */
public enum EsferaOrcamentaria {
    ORCAMENTOGERAL("1 - Orçamento Geral", EsferaOrcamentariaDTO.ORCAMENTOGERAL),
    ORCAMENTOFISCAL("2 - Orçamento Fiscal", EsferaOrcamentariaDTO.ORCAMENTOFISCAL),
    ORCAMENTOSEGURIDADESOCIAL("3 - Orçamento da Seguridade Social", EsferaOrcamentariaDTO.ORCAMENTOSEGURIDADESOCIAL);
    private String descricao;
    private EsferaOrcamentariaDTO toDto;

    EsferaOrcamentaria(String descricao, EsferaOrcamentariaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public EsferaOrcamentariaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
