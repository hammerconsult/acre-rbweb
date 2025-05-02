package br.com.webpublico.enums;

/**
 * Created by venom on 18/11/14.
 */
public enum CriterioDesempate {
    IDADE("Idade"),
    JURADO("Foi jurado em eleições"),
    MESARIO("Foi mesário em eleições"),
    DOADOR("É doador de sangue"),
    CARGO_PUBLICO("Já ocupa cargo público");
    private String descricao;

    CriterioDesempate(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
