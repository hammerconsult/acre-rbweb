package br.com.webpublico.enums;

/**
 * @author daniel
 */
public enum TipoOcupacaoCondominio {
    COMERCIAL("Comercial"),
    RESIDENCIAL("Residencial"),
    MISTO("Misto");

    String descricao;

    TipoOcupacaoCondominio(String descricao) {
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
