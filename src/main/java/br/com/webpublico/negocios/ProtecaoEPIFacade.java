package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EquipamentoEPI;
import br.com.webpublico.entidades.ProtecaoEPI;
import br.com.webpublico.interfaces.CRUD;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by carlos on 19/06/15.
 */
@Stateless
public class ProtecaoEPIFacade extends AbstractFacade<ProtecaoEPI> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ProtecaoEPIFacade() {
        super(ProtecaoEPI.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(ProtecaoEPI entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(ProtecaoEPI entity) {
        super.salvarNovo(entity);
    }

    public List<ProtecaoEPI> listaProtecaoEPI() {
        String hql = " select p from ProtecaoEPI p ";
        Query q = em.createQuery(hql);
        List<ProtecaoEPI> lista = q.getResultList();
        return lista;
    }

    public List<ProtecaoEPI> completaProtecao(String filtro) {
        String hql = " select p from ProtecaoEPI p where lower(trim(nome)) like :filtro ";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

}
