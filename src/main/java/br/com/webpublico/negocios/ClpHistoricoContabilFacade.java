/*
 * Codigo gerado automaticamente em Tue May 29 13:56:45 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ClpHistoricoContabil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ClpHistoricoContabilFacade extends AbstractFacade<ClpHistoricoContabil> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClpHistoricoContabilFacade() {
        super(ClpHistoricoContabil.class);
    }
}
