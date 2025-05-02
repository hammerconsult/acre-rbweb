package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 25/07/13
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
public enum TipoFonteRelatorio {
    ARIAL("Arial", "arial"),
    COURIER_NEW("Courier New", "courier-new"),
    SANS_SERIF("Sans Serif", "sans-serif"),
    TAHOMA("Tahoma", "tahoma"),
    TIMES_NEW_ROMAN("Times New Roman", "Times New Roman"),
    VERDANDA("Verdana", "verdana");

    private String descricao;
    private String value;

    private TipoFonteRelatorio(String descricao, String valeu) {
        this.descricao = descricao;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
