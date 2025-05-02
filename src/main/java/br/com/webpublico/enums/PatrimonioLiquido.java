package br.com.webpublico.enums;

/**
 * Created by mateus on 12/12/17.
 */
public enum PatrimonioLiquido {
    PUBLICO("Público"),
    PRIVADO("Privado");

    private String descricao;

    PatrimonioLiquido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static PatrimonioLiquido buscarPorNatureza(TipoEntidade natureza) {
        switch (natureza) {
            case EMPRESAPUBLICA:
            case SOCIEDADE_DE_ECONOMIA_MISTA:
                return PRIVADO;
            default:
                return PUBLICO;
        }
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isPublico() {
        return PUBLICO.equals(this);
    }

    public boolean isPrivado() {
        return PRIVADO.equals(this);
    }
}
