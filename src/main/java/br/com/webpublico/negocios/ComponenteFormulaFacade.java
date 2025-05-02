/*
 * Codigo gerado automaticamente em Wed Apr 20 15:09:37 BRT 2011
 * Gerador de Facace
*/

package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ComponenteFormula;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ComponenteFormulaFacade extends AbstractFacade<ComponenteFormula> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComponenteFormulaFacade() {
        super(ComponenteFormula.class);
    }
}
