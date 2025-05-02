package br.com.webpublico.nfse.domain.dtos.enums;

public enum GravidadeNfseDTO {
    ERRO("error"),
    ATENCAO("war"),
    INFORMACAO("info");
    private String descricao;

    GravidadeNfseDTO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {

        return descricao;
    }
}
