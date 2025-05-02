package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
public enum TipoFaleConoscoNfseDTO implements NfseEnumDTO {
    DENUNCIA("Denúncia"),
    RECLAMACAO("Reclamação"),
    SOLICITACAO("Solicitação"),
    SUGESTAO("Sugestão"),
    ELOGIO("Elogio");

    private String descricao;

    TipoFaleConoscoNfseDTO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
