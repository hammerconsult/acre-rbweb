/*
 * Codigo gerado automaticamente em Fri Oct 07 09:24:50 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemFaixaReferenciaFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ItemFaixaReferenciaFPFacade extends AbstractFacade<ItemFaixaReferenciaFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemFaixaReferenciaFPFacade() {
        super(ItemFaixaReferenciaFP.class);
    }
}
