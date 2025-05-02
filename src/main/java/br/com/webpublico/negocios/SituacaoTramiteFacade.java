/*
 * Codigo gerado automaticamente em Fri Mar 11 11:19:01 BRT 2011
 * Gerador de Facace
*/

package br.com.webpublico.negocios;

import br.com.webpublico.entidades.SituacaoTramite;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class SituacaoTramiteFacade extends AbstractFacade<SituacaoTramite> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SituacaoTramiteFacade() {
        super(SituacaoTramite.class);
    }
}
