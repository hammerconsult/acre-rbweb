package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EquipamentoEPC;
import br.com.webpublico.entidades.EquipamentoEPI;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by carlos on 04/09/15.
 */
@Stateless
public class EquipamentoEPCFacade extends AbstractFacade<EquipamentoEPC>{

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public EquipamentoEPCFacade() {
        super(EquipamentoEPC.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(EquipamentoEPC entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(EquipamentoEPC entity) {
        super.salvarNovo(entity);
    }

    public List<EquipamentoEPC> listaEquipamentoEPC() {
        String hql = " select epc from EquipamentoEPC epc ";
        Query q = em.createQuery(hql);
        List<EquipamentoEPC> lista = q.getResultList();
        return lista;
    }

    public List<EquipamentoEPC> listaEquipamentoEPCFiltrando(String filtro) {
        String sql = " select e.* from equipamentoepc e" +
                " where lower(trim(e.descricao)) like :filtro";
        Query q = em.createNativeQuery(sql, EquipamentoEPC.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public boolean existeEPCPorCodigo(String codigo) {
        String hql = "from EquipamentoEPC where codigo = :codigo";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }
}
