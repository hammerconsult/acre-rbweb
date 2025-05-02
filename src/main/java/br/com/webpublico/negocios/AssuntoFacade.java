/*
 * Codigo gerado automaticamente em Fri Mar 04 09:44:14 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Assunto;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AssuntoFacade extends AbstractFacade<Assunto> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AssuntoFacade() {
        super(Assunto.class);
    }
}
