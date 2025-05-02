/*
 * Codigo gerado automaticamente em Wed Apr 13 10:23:12 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Periodicidade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PeriodicidadeFacade extends AbstractFacade<Periodicidade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PeriodicidadeFacade() {
        super(Periodicidade.class);
    }
}
