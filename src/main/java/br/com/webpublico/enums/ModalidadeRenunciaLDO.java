/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.ModalidadeRenunciaLDODTO;

/**
 * @author arthur
 */
public enum ModalidadeRenunciaLDO {

    ANISTIA("Anistia", ModalidadeRenunciaLDODTO.ANISTIA),
    ISENCAO("Isenção", ModalidadeRenunciaLDODTO.ISENCAO),
    REMISSAO("Remissão", ModalidadeRenunciaLDODTO.REMISSAO),
    ANISTIA_ISENCAO("Anistia e Isenção", ModalidadeRenunciaLDODTO.ANISTIA_ISENCAO),
    ANISTIA_REMISSAO("Anistia e Remissão", ModalidadeRenunciaLDODTO.ANISTIA_REMISSAO),
    ANISTIA_ISENCAO_REMISSAO("Anistia, Isenção e Remissão", ModalidadeRenunciaLDODTO.ANISTIA_ISENCAO_REMISSAO);

    private String descricao;
    private ModalidadeRenunciaLDODTO toDto;

    ModalidadeRenunciaLDO(String descricao, ModalidadeRenunciaLDODTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public ModalidadeRenunciaLDODTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
