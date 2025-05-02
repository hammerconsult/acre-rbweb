package br.com.webpublico.repositorios.jdbc.chavenegocio;

import br.com.webpublico.entidades.ChaveNegocio;

/**
 * Created by Skywalker on 15/10/2015.
 */
public class ChaveNegocioBanco implements ChaveNegocio {

    private String numeroBanco;

    public ChaveNegocioBanco(String numeroBanco) {
        this.numeroBanco = numeroBanco;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChaveNegocioBanco that = (ChaveNegocioBanco) o;

        return !(numeroBanco != null ? !numeroBanco.equals(that.numeroBanco) : that.numeroBanco != null);

    }

    @Override
    public int hashCode() {
        return numeroBanco != null ? numeroBanco.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ChaveNegocioBanco{" +
            "numeroBanco='" + numeroBanco + '\'' +
            '}';
    }
}
