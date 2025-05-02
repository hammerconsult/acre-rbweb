/*
 * Codigo gerado automaticamente em Mon Mar 26 11:55:39 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.OcorrenciaDividaPublica;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class OcorrenciaDividaPublicaFacade extends AbstractFacade<OcorrenciaDividaPublica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcorrenciaDividaPublicaFacade() {
        super(OcorrenciaDividaPublica.class);
    }
}
