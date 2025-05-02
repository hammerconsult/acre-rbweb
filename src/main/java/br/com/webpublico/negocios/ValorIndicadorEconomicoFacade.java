/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ValorIndicadorEconomico;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author wiplash
 */
@Stateless
public class ValorIndicadorEconomicoFacade extends AbstractFacade<ValorIndicadorEconomico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ValorIndicadorEconomicoFacade() {
        super(ValorIndicadorEconomico.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
