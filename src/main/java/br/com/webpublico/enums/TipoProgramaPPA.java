/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoProgramaPPADTO;

/**
 * Determina o tipo de programa PPA:
 * <p/>
 * - finalísticos - resultam em bens ou serviços ofertados diretamente à
 * população; e
 * <p/>
 * - de apoio administrativo - contempla as despesas de natureza tipicamente
 * administrativa, que, embora contribuam para a consecução dos objetivos
 * dos outros programas, não são passíveis de apropriação a estes programas.
 *
 * @author arthur
 */
public enum TipoProgramaPPA {
    APOIO_ADMINISTRATIVO("Apoio Administrativo", TipoProgramaPPADTO.APOIO_ADMINISTRATIVO),
    FINALISTICO("Finalístico", TipoProgramaPPADTO.FINALISTICO);
    private String descricao;
    private TipoProgramaPPADTO toDto;

    TipoProgramaPPA(String descricao, TipoProgramaPPADTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoProgramaPPADTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

