/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

/**
 * @author boy
 */
@Entity

@Etiqueta("Carteira de Vacinação")
@Audited
public class CarteiraVacinacao extends DocumentoPessoal {

    @Etiqueta("Número")
    private String numero;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return getNumero();
    }
}
