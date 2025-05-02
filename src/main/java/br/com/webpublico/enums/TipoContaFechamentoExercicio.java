package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 13/11/14
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
public enum TipoContaFechamentoExercicio {
    RESULTADO_DIMINUTIVO("Resultado Diminutivo do Exercício"),
    RESULTADO_AUMENTATIVO("Resultado Aumentativo do Exercício"),
    TRASPORTE("Transporte");

    private String descricao;

    private TipoContaFechamentoExercicio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
