package br.com.webpublico.entidades.comum.consultasql;

import java.util.List;

/**
 * Created by renatoromanini on 01/09/15.
 */
public class ObjetoView {
    private Object valor;
    private List<ColunaView> colunas;

    public ObjetoView(Object valor, List<ColunaView> colunas) {
        this.valor = valor;
        this.colunas = colunas;
    }

    public List<ColunaView> getColunas() {
        return colunas;
    }

    public void setColunas(List<ColunaView> colunas) {
        this.colunas = colunas;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
