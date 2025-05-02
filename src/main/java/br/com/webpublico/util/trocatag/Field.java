package br.com.webpublico.util.trocatag;


public class Field {

    private Tag tag;
    private String value;

    public Field(Tag tag, String value) {
        this.tag = tag;
        this.value = value;
    }

    public Tag getTag() {
        return tag;
    }

    public String getValue() {
        return value;
    }
}
