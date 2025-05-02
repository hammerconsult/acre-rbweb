package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Metodologia;
import br.com.webpublico.singletons.SingletonGeradorCodigoRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by carlos on 27/05/15.
 */
@Stateless
public class MetodologiaFacade extends AbstractFacade<Metodologia> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SingletonGeradorCodigoRH singletonGeradorCodigoRH;

    public MetodologiaFacade() {
        super(Metodologia.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean existeMetodologiaPorNome(String nome) {
        String hql = "select m from Metodologia m where m.nome = :nome";
        Query q = em.createQuery(hql);
        q.setParameter("nome", nome);
        return !q.getResultList().isEmpty();
    }

    public boolean existeMetodologiaPorCodigo(String codigo) {
        String hql = "select m from Metodologia m where m.codigo = :codigo";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public List<Metodologia> completaMetodologia(String filtro) {
        String hql = "select m from Metodologia m where lower(trim(m.nome)) like :filtro" +
                " or lower(trim(m.descricao)) like :filtro";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public SingletonGeradorCodigoRH getSingletonGeradorCodigoRH() {
        return singletonGeradorCodigoRH;
    }
}
