/*
 * Codigo gerado automaticamente em Wed Aug 03 16:05:53 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoRegime;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TipoRegimeFacade extends AbstractFacade<TipoRegime> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoRegimeFacade() {
        super(TipoRegime.class);
    }
}
