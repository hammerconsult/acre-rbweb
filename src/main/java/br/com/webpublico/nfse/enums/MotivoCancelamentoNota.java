package br.com.webpublico.nfse.enums;

import br.com.webpublico.nfse.domain.dtos.enums.MotivoCancelamentoNfseDTO;

public enum MotivoCancelamentoNota {
    ERRO_EMISSAO("Erro na emissão", MotivoCancelamentoNfseDTO.ERRO_EMISSAO),
    ERRO_DADOS("Erro de dados na emissão", MotivoCancelamentoNfseDTO.ERRO_DADOS),
    SERVICO_NAO_PRESTADO("Serviço não prestado", MotivoCancelamentoNfseDTO.SERVICO_NAO_PRESTADO),
    ERRO_ASSINATURA("Erro na assinatura", MotivoCancelamentoNfseDTO.ERRO_EMISSAO),
    DUPLICIDADE("Duplicidade de Nota", MotivoCancelamentoNfseDTO.DUPLICIDADE),
    ERRO_PROCESSAMENTO("Erro no processamento", MotivoCancelamentoNfseDTO.ERRO_PROCESSAMENTO);

    private String descricao;
    private MotivoCancelamentoNfseDTO motivoDto;

    MotivoCancelamentoNota(String descricao, MotivoCancelamentoNfseDTO motivoDto) {
        this.motivoDto = motivoDto;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public MotivoCancelamentoNfseDTO toNfseDto() {
        return motivoDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
