/*
 * Codigo gerado automaticamente em Wed Nov 09 14:46:59 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MembroComissao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MembroComissaoFacade extends AbstractFacade<MembroComissao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MembroComissaoFacade() {
        super(MembroComissao.class);
    }
}
