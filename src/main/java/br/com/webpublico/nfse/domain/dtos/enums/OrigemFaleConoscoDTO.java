package br.com.webpublico.nfse.domain.dtos.enums;

public enum OrigemFaleConoscoDTO {

    NFSE("Nfs-e");

    private String descricao;

    OrigemFaleConoscoDTO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
