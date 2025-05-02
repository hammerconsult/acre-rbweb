package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

public class FichaCredorCNH {

    private String numero;
    private Date validade;
    private String categoria;

    public FichaCredorCNH() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
