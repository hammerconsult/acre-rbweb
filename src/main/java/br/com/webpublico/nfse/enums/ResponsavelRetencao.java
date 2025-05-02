package br.com.webpublico.nfse.enums;

import br.com.webpublico.nfse.domain.dtos.enums.ResponsavelRetencaoNfseDTO;

/**
 * @Author peixe on 25/09/2015.
 */
public enum ResponsavelRetencao {
    TOMADADOR("Tomador", ResponsavelRetencaoNfseDTO.TOMADADOR),
    INTERMEDIARIO("Intermediário", ResponsavelRetencaoNfseDTO.INTERMEDIARIO);

    String descricao;
    ResponsavelRetencaoNfseDTO dto;

    ResponsavelRetencao(String descricao, ResponsavelRetencaoNfseDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public String getDescricao() {
        return descricao;
    }

    public ResponsavelRetencaoNfseDTO toDto() {
        return dto;
    }
}
