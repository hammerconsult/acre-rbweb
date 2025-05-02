/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BloqueioParcelamento;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class BloqueioParcelamentoFacade extends AbstractFacade<BloqueioParcelamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public BloqueioParcelamentoFacade() {
        super(BloqueioParcelamento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
