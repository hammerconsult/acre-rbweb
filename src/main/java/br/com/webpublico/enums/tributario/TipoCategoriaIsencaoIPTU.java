package br.com.webpublico.enums.tributario;

public enum TipoCategoriaIsencaoIPTU {
    ISENCAO("Isenção"),
    IMUNIDADE("Imunidade");

    private String descricao;

    TipoCategoriaIsencaoIPTU(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
