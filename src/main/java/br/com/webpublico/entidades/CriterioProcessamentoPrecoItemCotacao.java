package br.com.webpublico.entidades;

/**
 * Created with IntelliJ IDEA.
 * User: Novo
 * Date: 29/06/15
 * Time: 10:15
 * To change this template use File | Settings | File Templates.
 */
public enum CriterioProcessamentoPrecoItemCotacao {

    MINIMO("Mínimo", "min"),
    MEDIO("Médio", "avg"),
    MEDIANO("Mediano", "median"),
    MAXIMO("Máximo", "max");

    private String descricao;
    private String criterio;

    private CriterioProcessamentoPrecoItemCotacao(String descricao, String criterio) {
        this.descricao = descricao;
        this.criterio = criterio;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCriterio() {
        return criterio;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
