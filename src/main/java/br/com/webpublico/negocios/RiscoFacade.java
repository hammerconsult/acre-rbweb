package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Risco;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by carlos on 17/06/15.
 */
@Stateless
public class RiscoFacade extends AbstractFacade<Risco> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public RiscoFacade(){
        super(Risco.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(Risco entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(Risco entity) {
        super.salvarNovo(entity);
    }

    public List<Risco> listaPorCodigo(Risco codigo) {
        String hql = " from Risco where codigo = :codigo ";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        List<Risco> lista = q.getResultList();
        return lista;
    }

    public List<Risco> listaRiscos() {
        String sql = " select *  from Risco ";
        Query q = em.createNativeQuery(sql, Risco.class);
        List<Risco> lista = q.getResultList();
        return lista;

    }

    public List<Risco> completaRisco(String filtro) {
        String hql = "from Risco where lower(trim(descricao)) like :filtro";
        Query q = em.createQuery(hql, Risco.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public Boolean existePorCodigo(Integer codigo) {
        String hql = " from Risco where codigo = :codigo ";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }
}
