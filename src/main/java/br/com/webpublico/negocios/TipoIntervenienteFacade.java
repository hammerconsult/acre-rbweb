/*
 * Codigo gerado automaticamente em Thu Apr 05 09:30:03 BRT 2012
 * Gerador de Facace
*/

package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoInterveniente;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TipoIntervenienteFacade extends AbstractFacade<TipoInterveniente> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoIntervenienteFacade() {
        super(TipoInterveniente.class);
    }
}
