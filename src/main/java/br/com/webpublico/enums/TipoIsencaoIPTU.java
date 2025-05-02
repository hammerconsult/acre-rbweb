package br.com.webpublico.enums;

/**
 * @author Fabio
 */
public enum TipoIsencaoIPTU {

    PREDIAL("1 - Imposto Predial"),
    TERRITORIAL("2 - Imposto Territorial"),
    AMBOS("3 - Ambos");

    private String descricao;

    private TipoIsencaoIPTU(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
