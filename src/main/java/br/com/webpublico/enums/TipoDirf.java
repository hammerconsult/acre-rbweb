package br.com.webpublico.enums;

/**
 * @author Mateus
 * @since 23/02/2016 14:28
 */
public enum TipoDirf {

    RH("RH"),
    CONTABIL("Contábil");

    TipoDirf(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

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
