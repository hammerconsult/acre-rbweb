/*
 * Codigo gerado automaticamente em Fri Mar 04 11:23:37 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoProcesso;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TipoProcessoFacade extends AbstractFacade<TipoProcesso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoProcessoFacade() {
        super(TipoProcesso.class);
    }
}
