/*
 * Codigo gerado automaticamente em Thu Apr 05 08:46:51 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ClassificacaoCredor;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ClassificacaoCredorFacade extends AbstractFacade<ClassificacaoCredor> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClassificacaoCredorFacade() {
        super(ClassificacaoCredor.class);
    }


}
