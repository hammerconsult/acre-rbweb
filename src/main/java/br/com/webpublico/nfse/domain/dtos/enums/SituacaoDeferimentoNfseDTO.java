package br.com.webpublico.nfse.domain.dtos.enums;

public enum SituacaoDeferimentoNfseDTO {
    DEFERIDO("Deferido"), NAO_DEFERIDO("Não Deferido"), EM_ANALISE("Em análise");
    private String descricao;

    SituacaoDeferimentoNfseDTO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
