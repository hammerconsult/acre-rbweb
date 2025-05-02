/*
 * Codigo gerado automaticamente em Wed Jan 18 11:33:14 BRST 2012
 * Gerador de Facace
*/

package br.com.webpublico.negocios;

import br.com.webpublico.entidades.NaturezaRendimento;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class NaturezaRendimentoFacade extends AbstractFacade<NaturezaRendimento> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NaturezaRendimentoFacade() {
        super(NaturezaRendimento.class);
    }
}
