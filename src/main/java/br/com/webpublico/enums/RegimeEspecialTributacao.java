package br.com.webpublico.enums;

public enum RegimeEspecialTributacao {

    PADRAO("Padrão"),
    INSTITUICAO_FINANCEIRA("Instiruição Finanaceira");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private RegimeEspecialTributacao(String descricao) {
        this.descricao = descricao;
    }

}
