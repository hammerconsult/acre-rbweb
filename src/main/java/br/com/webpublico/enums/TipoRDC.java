package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: JULIO-MGA
 * Date: 24/03/14
 * Time: 11:43
 * To change this template use File | Settings | File Templates.
 */
public enum TipoRDC {
    ABERTO("Aberto"),
    FECHADO("Fechado"),
    COMBINADO("Combinado"),
    ABERTA_COM_REGISTRO_DE_PRECO("Aberta com registro de preço "),
    FECHADA_COM_REGISTRO_DE_PRECO("Fechada com registro de preço"),
    COMBINADO_COM_REGISTRO_DE_PRECO("Combinado com Registro de Preço");

    private String descricao;

    private TipoRDC(String descricao) {
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
