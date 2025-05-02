/*
 * Codigo gerado automaticamente em Tue Feb 22 10:18:53 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CotacaoMoeda;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CotacaoMoedaFacade extends AbstractFacade<CotacaoMoeda> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CotacaoMoedaFacade() {
        super(CotacaoMoeda.class);
    }
}
