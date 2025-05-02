package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
public enum LocalExecucaoTrabalho implements NfseEnumDTO {
    LOCAL("No Local"),
    DOMICIO_PRESTADOR("No domicilio do prestador"),
    ESTABELECIMENTO_PRESTADOR("No estabelecimento do prestador");

    private String descricao;

    LocalExecucaoTrabalho(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
