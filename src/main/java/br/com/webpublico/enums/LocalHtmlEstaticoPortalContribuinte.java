
package br.com.webpublico.enums;

/**
 * @author Pedro
 */

public enum LocalHtmlEstaticoPortalContribuinte {
    CENTRO_ATENDIMENTO("Centro de atendimento", "/centros-atendimento/");

    private String descricao;
    private String caminhoUrl;

    LocalHtmlEstaticoPortalContribuinte(String descricao, String caminhoUrl) {
        this.descricao = descricao;
        this.caminhoUrl = caminhoUrl;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCaminhoUrl() {
        return caminhoUrl;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
