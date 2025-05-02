/*
 * Codigo gerado automaticamente em Wed Oct 17 17:16:18 BRT 2012
 * Gerador de Facace
*/

package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoContaExtra;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TipoContaExtraFacade extends AbstractFacade<TipoContaExtra> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoContaExtraFacade() {
        super(TipoContaExtra.class);
    }
}
