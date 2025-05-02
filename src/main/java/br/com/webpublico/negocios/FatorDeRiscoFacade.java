package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FatorDeRisco;
import br.com.webpublico.entidades.Risco;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by carlos on 17/06/15.
 */
@Stateless
public class FatorDeRiscoFacade extends AbstractFacade<FatorDeRisco> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private RiscoFacade riscoFacade;

    public FatorDeRiscoFacade() {
        super(FatorDeRisco.class);
    }

    public RiscoFacade getRiscoFacade() {
        return riscoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(FatorDeRisco entity) {
        super.salvarNovo(entity);
    }

    @Override
    public void salvar(FatorDeRisco entity) {
        super.salvar(entity);
    }

    public List<FatorDeRisco> listaPorCodigo(FatorDeRisco codigo) {
        String hql = " from FatorDeRisco where codigo = :codigo ";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        List<FatorDeRisco> lista = q.getResultList();
        return lista;
    }

    public List<FatorDeRisco> completaFatorRisco(String filtro, Risco risco) {
//        String hql = "from FatorDeRisco where lower(trim(descricao)) like :filtro";
//        Query q = em.createQuery(hql);

        String sql = " select fr.* from fatorderisco fr " +
                " inner join risco r on r.id = fr.risco_id " +
                " where r.id = :risco " +
                " and lower(trim(fr.descricao)) like :filtro ";
        Query q = em.createNativeQuery(sql, FatorDeRisco.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("risco", risco.getId());
        return q.getResultList();
    }

    public Boolean existePorCodigo(Integer codigo) {
        String hql = " from FatorDeRisco where codigo = :codigo ";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }


}
