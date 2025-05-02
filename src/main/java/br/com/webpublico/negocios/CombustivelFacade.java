/*
 * Codigo gerado automaticamente em Tue Aug 23 01:14:14 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Combustivel;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CombustivelFacade extends AbstractFacade<Combustivel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CombustivelFacade() {
        super(Combustivel.class);
    }
}
