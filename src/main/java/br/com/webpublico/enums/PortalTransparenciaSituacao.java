package br.com.webpublico.enums;

/**
 * Created by renat on 26/04/2016.
 */
public enum PortalTransparenciaSituacao {
    PUBLICADO("Públicado"),
    NAO_PUBLICADO("Não Públicado");
    private String descricao;

    PortalTransparenciaSituacao(String descricao) {
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
