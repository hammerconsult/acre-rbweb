package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class ListenerGaranteEnderecoPrincipal {

    @PrePersist
    @PreUpdate
    public void setaEnderecoPrincipal(Pessoa pessoa) {
        pessoa.setEnderecoPrincipal(pessoa.getEnderecoCorrespondencia());
    }
}
