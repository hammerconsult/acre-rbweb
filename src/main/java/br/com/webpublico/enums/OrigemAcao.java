package br.com.webpublico.enums;

public enum OrigemAcao {

    PODER_EXECUTIVO("Poder Executivo"),
    PODER_LEGISLATIVO_EMENDA_ADITIVA("Poder Legislativo - Emenda Aditiva"),
    PODER_LEGISLATIVO_EMENDA_MODIFICATIVA("Poder Legislativo - Emenda Modificativa");

    private String descricao;

    private OrigemAcao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
