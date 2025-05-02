/*
 * Codigo gerado automaticamente em Tue Mar 27 11:43:20 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.InvalidezAposentado;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class InvalidezAposentadoFacade extends AbstractFacade<InvalidezAposentado> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InvalidezAposentadoFacade() {
        super(InvalidezAposentado.class);
    }
}
