package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 30/03/15
 * Time: 10:21
 * To change this template use File | Settings | File Templates.
 */
public enum ConcedidaRecebida {

    CONCEDIDA("Concedida"),
    RECEBIDA("Recebida");
    private String descricao;

    private ConcedidaRecebida(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
