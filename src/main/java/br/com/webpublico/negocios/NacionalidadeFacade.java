/*
 * Codigo gerado automaticamente em Tue Feb 08 13:52:44 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Nacionalidade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class NacionalidadeFacade extends AbstractFacade<Nacionalidade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NacionalidadeFacade() {
        super(Nacionalidade.class);
    }
}
