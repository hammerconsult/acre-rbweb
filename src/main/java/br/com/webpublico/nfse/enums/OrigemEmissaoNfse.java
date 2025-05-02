package br.com.webpublico.nfse.enums;

public enum OrigemEmissaoNfse {
    WEB("Web"),
    APP("App"),
    RPS("RPS");
    private String descricao;

    OrigemEmissaoNfse(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
