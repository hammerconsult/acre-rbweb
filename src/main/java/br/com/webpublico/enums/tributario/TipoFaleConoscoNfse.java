package br.com.webpublico.enums.tributario;

import br.com.webpublico.nfse.domain.dtos.enums.TipoFaleConoscoNfseDTO;
import br.com.webpublico.nfse.enums.NfseEnum;
import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public enum TipoFaleConoscoNfse implements NfseEnum<TipoFaleConoscoNfse> {
    DENUNCIA("Denúncia", TipoFaleConoscoNfseDTO.DENUNCIA),
    RECLAMACAO("Reclamação", TipoFaleConoscoNfseDTO.RECLAMACAO),
    SOLICITACAO("Solicitação", TipoFaleConoscoNfseDTO.SOLICITACAO),
    SUGESTAO("Sugestão", TipoFaleConoscoNfseDTO.SUGESTAO),
    ELOGIO("Elogio", TipoFaleConoscoNfseDTO.ELOGIO);

    private String descricao;
    private TipoFaleConoscoNfseDTO dto;

    TipoFaleConoscoNfse(String descricao, TipoFaleConoscoNfseDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public NfseEnumDTO toDto() {
        return dto;
    }
}
