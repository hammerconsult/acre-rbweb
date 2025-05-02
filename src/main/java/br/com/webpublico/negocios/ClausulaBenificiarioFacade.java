/*
 * Codigo gerado automaticamente em Thu Apr 05 11:31:08 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ClausulaBenificiario;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ClausulaBenificiarioFacade extends AbstractFacade<ClausulaBenificiario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClausulaBenificiarioFacade() {
        super(ClausulaBenificiario.class);
    }

    @Override
    public List<ClausulaBenificiario> lista() {

        /*
         * TODO Mudar forma de lista. ordenar por id decrescente (Ultimo salvo)
         */


        String hql = "from  ClausulaBenificiario obj order by obj.id";
        Query q = getEntityManager().createQuery(hql);

        return q.getResultList();
    }


}
