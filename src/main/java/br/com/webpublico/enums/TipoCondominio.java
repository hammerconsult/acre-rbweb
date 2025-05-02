package br.com.webpublico.enums;

/**
 * @author daniel
 */
public enum TipoCondominio {
    HORIZONTAL("Horizontal"),
    VERTICAL("Vertical");

    private String descricao;

    TipoCondominio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
