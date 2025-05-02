/*
 * Codigo gerado automaticamente em Fri Feb 11 13:51:59 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConstrucaoExistenteSeker;
import br.com.webpublico.entidades.DiretorSecretarioSeker;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ConstrucaoExistenteSekerFacade extends AbstractFacade<ConstrucaoExistenteSeker> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConstrucaoExistenteSekerFacade() {
        super(ConstrucaoExistenteSeker.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public List<ConstrucaoExistenteSeker> buscarConstrucaoExistenteSeker() {
        return em.createNativeQuery("select * from CONSTRUCAOEXISTENTESEKER", ConstrucaoExistenteSeker.class).getResultList();
    }
}
