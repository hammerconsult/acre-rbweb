package br.com.webpublico.enums.rh.processo;

public enum TipoPad {
    SUMARIO("Sumário"),
    ORDINARIO("Ordinário");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    TipoPad(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
