package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoReducao;
import br.com.webpublico.entidades.TipoVinculoBem;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 08/06/15
 * Time: 17:11
 * To change this template use File | Settings | File Templates.
 */

@Stateless
public class TipoVinculoBemFacade extends AbstractFacade<TipoVinculoBem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TipoVinculoBemFacade() {
        super(TipoVinculoBem.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoVinculoBem recuperarPorDescricao(String descricao) {
        if (descricao == null || descricao.isEmpty()) {
            return null;
        }
        try{
            return (TipoVinculoBem) em.createQuery("from TipoVinculoBem where trim(lower(descricao)) like :desc ").setParameter("desc", descricao.toLowerCase().trim()).getSingleResult();
        }catch (NoResultException re){
           return null;
        }
    }
}
