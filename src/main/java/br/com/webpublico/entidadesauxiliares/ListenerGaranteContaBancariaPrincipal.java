package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 18/03/15
 * Time: 17:51
 * To change this template use File | Settings | File Templates.
 */
public class ListenerGaranteContaBancariaPrincipal {

    @PrePersist
    @PreUpdate
    public void setaContaBancariaPrincipal(Pessoa pessoa) {
        pessoa.setContaCorrentePrincipal(pessoa.getContaBancariaPrincipalAdicionada());
    }
}
