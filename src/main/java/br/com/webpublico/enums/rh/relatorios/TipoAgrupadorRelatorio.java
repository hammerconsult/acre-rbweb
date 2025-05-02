package br.com.webpublico.enums.rh.relatorios;

/**
 * Created by Edi on 23/03/2016.
 */
public enum TipoAgrupadorRelatorio {

    GERAL("Geral"),
    POR_ORGAO("Por Órgão");
    private String descricao;

    TipoAgrupadorRelatorio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
