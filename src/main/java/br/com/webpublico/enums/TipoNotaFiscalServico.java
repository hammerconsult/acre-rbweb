/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.nfse.domain.dtos.enums.TipoNotaFiscalServicoNfseDTO;
import br.com.webpublico.nfse.enums.NfseEnum;

/**
 * @author fabio
 */
public enum TipoNotaFiscalServico implements NfseEnum {
    ELETRONICA("Sim", TipoNotaFiscalServicoNfseDTO.ELETRONICA),
    NAO_EMITE("Não", TipoNotaFiscalServicoNfseDTO.NAO_EMITE);

    private final String descricao;
    private final TipoNotaFiscalServicoNfseDTO dto;

    TipoNotaFiscalServico(String descricao, TipoNotaFiscalServicoNfseDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public TipoNotaFiscalServicoNfseDTO toDto() {
        return this.dto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
