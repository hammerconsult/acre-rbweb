package br.com.webpublico.ws.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: scarpini
 * Date: 30/03/14
 * Time: 15:33
 * To change this template use File | Settings | File Templates.
 */
public class WSEventoCalculadoImovel implements Serializable {
    private String descricao;
    private String identificacao;
    private String valor;

    public WSEventoCalculadoImovel() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }
}
