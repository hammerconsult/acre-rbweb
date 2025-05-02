package br.com.webpublico.enums.rh.previdencia;

public enum TipoContribuicaoBBPrev {
    SERVIDOR("Servidor"),
    PATROCINADOR("Patrocinador");
    private String descricao;

    TipoContribuicaoBBPrev(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
