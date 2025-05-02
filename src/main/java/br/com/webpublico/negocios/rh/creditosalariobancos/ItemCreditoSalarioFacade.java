/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.creditosalariobancos;


import br.com.webpublico.entidades.rh.creditodesalario.ItemCreditoSalario;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ItemCreditoSalarioFacade extends AbstractFacade<ItemCreditoSalario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ItemCreditoSalarioFacade() {
        super(ItemCreditoSalario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ItemCreditoSalario salvarMerge(ItemCreditoSalario ItemCreditoSalario) {
        return em.merge(ItemCreditoSalario);
    }
}
