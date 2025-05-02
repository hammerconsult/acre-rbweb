/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoItemCertameDTO;

/**
 * @author renato
 */
public enum SituacaoItemCertame {

    VENCEDOR("Vencedor", true, SituacaoItemCertameDTO.VENCEDOR),
    CLASSIFICADO("Classificado", true, SituacaoItemCertameDTO.CLASSIFICADO),
    DESCLASSIFICADO("Desclassificado", true, SituacaoItemCertameDTO.DESCLASSIFICADO),
    EMPATE("Empate", true, SituacaoItemCertameDTO.EMPATE),
    FRUSTRADO("Frustrado", true, SituacaoItemCertameDTO.FRUSTRADO),
    INEXEQUIVEL("Inexequível", true, SituacaoItemCertameDTO.INEXEQUIVEL),
    NAO_COTADO("Não Cotado", false, SituacaoItemCertameDTO.NAO_COTADO);

    private String descricao;
    private boolean recebeAlteracao;
    private SituacaoItemCertameDTO toDto;

    SituacaoItemCertame(String descricao, boolean recebeAlteracao, SituacaoItemCertameDTO toDto) {
        this.descricao = descricao;
        this.recebeAlteracao = recebeAlteracao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoItemCertameDTO getToDto() {
        return toDto;
    }

    public boolean isRecebeAlteracao() {
        return recebeAlteracao;
    }

    @Override
    public String toString() {
        return descricao;
    }


}
