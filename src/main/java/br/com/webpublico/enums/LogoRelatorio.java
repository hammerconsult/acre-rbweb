package br.com.webpublico.enums;

public enum LogoRelatorio {
    PREFEITURA("Prefeitura"),
    CAMARA("Câmara");
    private String descricao;

    LogoRelatorio(String descricao) {
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
