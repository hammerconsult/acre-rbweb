package br.com.webpublico.entidades.comum.consultasql;

/**
 * Created by renatoromanini on 01/09/15.
 */
public class ParametroView {
    private String condicao;
    private Object valor;
    private String operacao;

    public ParametroView(String condicao, Object valor) {
        this.condicao = condicao;
        this.valor = valor;
        this.operacao = " = ";
    }

    public ParametroView(String condicao, Object valor, String operador) {
        this.condicao = condicao;
        this.valor = valor;
        this.operacao = operador;
    }

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }
}
