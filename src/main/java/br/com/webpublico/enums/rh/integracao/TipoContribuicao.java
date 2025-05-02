package br.com.webpublico.enums.rh.integracao;

public enum TipoContribuicao {

    NAO_SE_APLICA("Não se Aplica", 0),
    PERCENTUAL_UNICO("Percentual Único", 1),
    PERCENTUAL_DIFERENCIADO("Percentual Diferenciado", 2);

    private String descricao;
    private Integer codigo;

    TipoContribuicao(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public static TipoContribuicao buscarPorCodigo(Integer codigo) {
        if (codigo != null) {
            for (TipoContribuicao tipoContribuicaoEquiplano : values()) {
                if (tipoContribuicaoEquiplano.getCodigo().equals(codigo)) {
                    return tipoContribuicaoEquiplano;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
