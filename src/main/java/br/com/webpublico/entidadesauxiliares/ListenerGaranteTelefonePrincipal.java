package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 18/03/15
 * Time: 08:55
 * To change this template use File | Settings | File Templates.
 */
public class ListenerGaranteTelefonePrincipal {

    @PrePersist
    @PreUpdate
    public void setaTelefonePrincipal(Pessoa pessoa) {
        pessoa.setTelefonePrincipal(pessoa.getTelefonePrincipalAdicionado());
    }
}
