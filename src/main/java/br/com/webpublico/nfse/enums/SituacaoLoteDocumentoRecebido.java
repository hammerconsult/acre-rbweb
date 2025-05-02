package br.com.webpublico.nfse.enums;

import br.com.webpublico.nfse.domain.dtos.LoteImportacaoDocumentoRecebidoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;

public enum SituacaoLoteDocumentoRecebido implements NfseEntity {
    PROCESSADO("Processado", LoteImportacaoDocumentoRecebidoNfseDTO.Situacao.PROCESSADO),
    AGUARDANDO("Aguardando", LoteImportacaoDocumentoRecebidoNfseDTO.Situacao.AGUARDANDO),
    INCONSISTENTE("Iconsistênte", LoteImportacaoDocumentoRecebidoNfseDTO.Situacao.INCONSISTENTE);

    private String descricao;
    private LoteImportacaoDocumentoRecebidoNfseDTO.Situacao toDto;

    SituacaoLoteDocumentoRecebido(String descricao, LoteImportacaoDocumentoRecebidoNfseDTO.Situacao toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public LoteImportacaoDocumentoRecebidoNfseDTO.Situacao toNfseDto() {
        return toDto;
    }
}
