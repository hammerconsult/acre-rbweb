package br.com.webpublico.enums.administrativo;

public enum UrlPncp {

    PNCP_HOMOLOGACAO("https://treina.pncp.gov.br"),
    PNCP_PRODUCAO("https://pncp.gov.br");
    private String descricao;

    UrlPncp(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
