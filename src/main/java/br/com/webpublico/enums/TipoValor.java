package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 30/04/14
 * Time: 11:07
 * Valor Normal os valores são proporcionalizados normalmente conforme dias trabalhados e ou dias de lançamentos
 * Valor Integral não proporcionaliza o valor, independente dos dias trabalhados, calcula o valor cheio.
 */
public enum TipoValor {

    NORMAL("Normal"),
    INTEGRAL("Integral");

    private String descricao;

    private TipoValor(String descricao) {
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
