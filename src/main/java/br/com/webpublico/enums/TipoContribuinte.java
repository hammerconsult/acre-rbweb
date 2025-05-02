package br.com.webpublico.enums;

import br.com.webpublico.nfse.domain.dtos.enums.TipoContribuinteNfseDTO;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 19/07/13
 * Time: 14:08
 * To change this template use File | Settings | File Templates.
 */
public enum TipoContribuinte {
    EVENTUAL("Eventual", TipoContribuinteNfseDTO.EVENTUAL),
    PERMANENTE("Permanente", TipoContribuinteNfseDTO.PERMANENTE);

    private String descricao;
    private TipoContribuinteNfseDTO nfseDTO;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    private TipoContribuinte(String descricao, TipoContribuinteNfseDTO nfseDTO) {
        this.descricao = descricao;
        this.nfseDTO = nfseDTO;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public TipoContribuinteNfseDTO getNfeDTO() {
        return nfseDTO;
    }
}
