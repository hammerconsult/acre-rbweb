package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.nfse.domain.dtos.NfseDTO;

public enum SituacaoLoteRps implements NfseDTO {
    PROCESSADO("Processado", 3), AGUARDANDO("Aguardando", 1), INCONSISTENTE("Iconsistênte", 2);

    private String descricao;
    private Integer codigo;

    SituacaoLoteRps(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }
}
