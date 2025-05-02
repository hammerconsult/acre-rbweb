package br.com.webpublico.nfse.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.nfse.domain.dtos.enums.TipoMovimentoMensalNfseDTO;

/**
 * Created by Buzatto on 20/12/2016.
 */
public enum TipoMovimentoMensal implements EnumComDescricao {

    NORMAL("Normal", TipoMovimentoMensalNfseDTO.NORMAL),
    SUBSTITUICAO("Substituição", TipoMovimentoMensalNfseDTO.NORMAL),
    INSTITUICAO_FINANCEIRA("Instituição Financeira", TipoMovimentoMensalNfseDTO.INSTITUICAO_FINANCEIRA),
    RETENCAO("Retenção", TipoMovimentoMensalNfseDTO.RETENCAO),
    ISS_RETIDO("ISS Retido", TipoMovimentoMensalNfseDTO.ISS_RETIDO);

    private String descricao;
    private TipoMovimentoMensalNfseDTO dto;

    TipoMovimentoMensal(String descricao, TipoMovimentoMensalNfseDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoMovimentoMensalNfseDTO toDto() {
        return dto;
    }
}
