package br.com.webpublico.repositorios.jdbc.chavenegocio;

import br.com.webpublico.entidades.ChaveNegocio;

import java.util.Objects;

public class ChaveNegocioUsuarioWeb implements ChaveNegocio {

    private String login;

    public ChaveNegocioUsuarioWeb(String login) {
        this.login = login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChaveNegocioUsuarioWeb that = (ChaveNegocioUsuarioWeb) o;
        return Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {

        return Objects.hash(login);
    }
}
