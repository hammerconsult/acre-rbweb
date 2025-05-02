package br.com.webpublico.consultaentidade;

public enum TipoAlinhamento {

    ESQUERDA("Esquerda", "alinhamentoEsquerda", "left"),
    CENTRO("Centro", "alinhamentoCentralizado", "center"),
    DIREITA("Direita", "alinhamentoDireita", "right");

    private final String descricao;
    private final String css;
    private final String style;

    TipoAlinhamento(String descricao, String css, String style) {
        this.descricao = descricao;
        this.css = css;
        this.style = style;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCss() {
        return css;
    }

    public String getStyle() {
        return style;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean is(String name) {
        return this.name().equals(name);
    }
}

