package br.com.webpublico.enums;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 15/01/14
 * Time: 20:45
 * To change this template use File | Settings | File Templates.
 */
public enum StatusLancamentoOutorga implements Serializable{
    LANCADO("Lançado"),
    ESTORNADO("Estornado");

    public String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    private StatusLancamentoOutorga(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
