package br.com.webpublico.nfse.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.nfse.domain.dtos.LoteImportacaoRPSNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;

public enum SituacaoLoteRps implements NfseEntity, EnumComDescricao {
    PROCESSADO("Processado", LoteImportacaoRPSNfseDTO.SituacaoLoteRps.PROCESSADO, 3),
    AGUARDANDO("Aguardando", LoteImportacaoRPSNfseDTO.SituacaoLoteRps.AGUARDANDO, 1),
    INCONSISTENTE("Inconsistente", LoteImportacaoRPSNfseDTO.SituacaoLoteRps.INCONSISTENTE, 2);

    private String descricao;
    private LoteImportacaoRPSNfseDTO.SituacaoLoteRps toDto;
    private Integer codigoWs;

    SituacaoLoteRps(String descricao, LoteImportacaoRPSNfseDTO.SituacaoLoteRps toDto, Integer codigoWs) {
        this.descricao = descricao;
        this.toDto = toDto;
        this.codigoWs = codigoWs;
    }

    public String getDescricao() {
        return descricao;
    }

    public LoteImportacaoRPSNfseDTO.SituacaoLoteRps toNfseDto() {
        return toDto;
    }

    public Integer getCodigoWs() {
        return codigoWs;
    }
}
