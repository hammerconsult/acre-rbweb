/*
 * Codigo gerado automaticamente em Wed Jun 08 11:23:49 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.RiscoFiscalLDO;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class RiscoFiscalLDOFacade extends AbstractFacade<RiscoFiscalLDO> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RiscoFiscalLDOFacade() {
        super(RiscoFiscalLDO.class);
    }
}
