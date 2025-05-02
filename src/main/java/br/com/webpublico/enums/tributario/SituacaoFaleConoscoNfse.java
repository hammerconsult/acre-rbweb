package br.com.webpublico.enums.tributario;

import br.com.webpublico.nfse.domain.dtos.enums.SituacaoFaleConoscoNfseDTO;
import br.com.webpublico.nfse.enums.NfseEnum;
import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public enum SituacaoFaleConoscoNfse implements NfseEnum<SituacaoFaleConoscoNfseDTO> {

    ABERTO("Aberto", SituacaoFaleConoscoNfseDTO.ABERTO),
    ENCERRADO("Encerrado", SituacaoFaleConoscoNfseDTO.ENCERRADO);

    private String descricao;
    private SituacaoFaleConoscoNfseDTO dto;

    SituacaoFaleConoscoNfse(String descricao, SituacaoFaleConoscoNfseDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAberto() {
        return SituacaoFaleConoscoNfse.ABERTO.equals(this);
    }

    public boolean isEncerrado() {
        return SituacaoFaleConoscoNfse.ENCERRADO.equals(this);
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
