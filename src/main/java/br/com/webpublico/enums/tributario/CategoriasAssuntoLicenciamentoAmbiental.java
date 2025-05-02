package br.com.webpublico.enums.tributario;

public enum CategoriasAssuntoLicenciamentoAmbiental {
    CATEGORIA_I("Categoria I"),
    CATEGORIA_II("Categoria II"),
    CATEGORIA_III("Categoria III"),
    CATEGORIA_IV("Categoria IV"),
    PEQUENO_PORTE("Pequeno Porte"),
    MEDIO_PORTE("Médio Porte"),
    GRANDE_PORTE("Grande Porte"),;

    private final String descricao;

    CategoriasAssuntoLicenciamentoAmbiental(String descricao) {
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
