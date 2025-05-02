package br.com.webpublico.enums;

public enum TipoConsulta {
    SINTETICA("Sintética"),
    ANALITICA("Analítica");

    private String descricao;

    TipoConsulta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isConsultaSintetica() {
        return TipoConsulta.SINTETICA.equals(this);
    }

    public boolean isConsultaAnalitica() {
        return TipoConsulta.ANALITICA.equals(this);
    }
}
