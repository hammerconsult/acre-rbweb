package br.com.webpublico.nfse.domain.dtos.enums;

public enum TipoProfissionalNfseDTO {
    ENGENHEIRO("Engenheiro"),
    ARQUITETO("Arquiteto");

    private String descricao;

    TipoProfissionalNfseDTO(String descricao) {
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
