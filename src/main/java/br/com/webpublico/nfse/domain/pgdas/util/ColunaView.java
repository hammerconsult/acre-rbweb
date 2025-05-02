package br.com.webpublico.nfse.domain.pgdas.util;

public class ColunaView {

    private Object valor;
    private String nomeColuna;

    public ColunaView(Object valor, String nomeColuna) {
        this.valor = valor;
        this.nomeColuna = nomeColuna;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public String getNomeColuna() {
        return nomeColuna;
    }

    public void setNomeColuna(String nomeColuna) {
        this.nomeColuna = nomeColuna;
    }
}
