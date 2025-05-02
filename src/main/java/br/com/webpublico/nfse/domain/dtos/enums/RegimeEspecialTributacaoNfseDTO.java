package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
public enum RegimeEspecialTributacaoNfseDTO implements NfseEnumDTO {
    PADRAO("Padrão"),
    INSTITUICAO_FINANCEIRA("Instiruição Finanaceira");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    RegimeEspecialTributacaoNfseDTO(String descricao) {
        this.descricao = descricao;
    }
}
