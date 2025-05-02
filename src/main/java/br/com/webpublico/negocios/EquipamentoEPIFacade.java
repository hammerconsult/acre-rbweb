package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EquipamentoEPI;
import br.com.webpublico.entidades.ProtecaoEPI;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by carlos on 18/06/15.
 */
@Stateless
public class EquipamentoEPIFacade extends AbstractFacade<EquipamentoEPI> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ProtecaoEPIFacade protecaoEPIFacade;

    public EquipamentoEPIFacade() {
        super(EquipamentoEPI.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(EquipamentoEPI entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(EquipamentoEPI entity) {
        super.salvarNovo(entity);
    }

    public List<EquipamentoEPI> listaEquipamentoEPI() {
        String hql = " select epi from EquipamentoEPI epi ";
        Query q = em.createQuery(hql);
        List<EquipamentoEPI> lista = q.getResultList();
        return lista;
    }

    public List<EquipamentoEPI> completaEquipamentoEPI(String filtro, ProtecaoEPI protecao) {
        String sql = " select e.* from equipamentoepi e" +
                " inner join protecaoepi p on p.id = e.protecaoepi_id" +
                " where p.id = :protecao" +
                " and lower(trim(e.equipamento)) like :filtro";
        Query q = em.createNativeQuery(sql, EquipamentoEPI.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("protecao", protecao.getId());
        return q.getResultList();
    }

    public ProtecaoEPIFacade getProtecaoEPIFacade() {
        return protecaoEPIFacade;
    }
}
