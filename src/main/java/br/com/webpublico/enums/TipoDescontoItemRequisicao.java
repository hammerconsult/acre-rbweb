package br.com.webpublico.enums;

public enum TipoDescontoItemRequisicao {
    ARREDONDAR("Arredondar", 4),
    NAO_ARREDONDAR("Não Arredondar", 8),
    TRUNCAR("Truncar", 4),
    MATERIAL_PERMANTENTE("Material Permanente", 2);
    private String descricao;
    private int scale;

    TipoDescontoItemRequisicao(String descricao, int scale) {
        this.descricao = descricao;
        this.scale = scale;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getScale() {
        return scale;
    }

    public boolean isArredondar() {
        return ARREDONDAR.equals(this);
    }

    public boolean isNaoArredondar() {
        return NAO_ARREDONDAR.equals(this);
    }

    public boolean isTruncar() {
        return TRUNCAR.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
