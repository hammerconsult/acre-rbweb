/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.MetasFiscais;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 *
 *
 */
@Stateless
public class MetasFiscaisFacade extends AbstractFacade<MetasFiscais> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LDOFacade ldoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MetasFiscaisFacade() {
        super(MetasFiscais.class);
    }

    @Override
    public MetasFiscais recuperar(Object id) {
        MetasFiscais mf = em.find(MetasFiscais.class, id);
        return mf;
    }

//    public boolean validarMetasFiscaisParaMesmaLDO(MetasFiscais selecionado) {
//        String sql = " select * from metasfiscais mf " +
//                " where mf.ldo_id = :idLDO";
//        if(selecionado.getId() != null){
//            sql += " and mf.id <> :idMetasFiscais";
//        }
//
//        Query consulta = getEntityManager().createNativeQuery(sql);
//        consulta.setParameter("idLDO", selecionado.getLdo().getId());
//        if(selecionado.getId() != null){
//            consulta.setParameter("idMetasFiscais", selecionado.getId());
//        }
//        if (!consulta.getResultList().isEmpty()) {
//            return false;
//        }
//        return true;
//    }

    public boolean validarMetasFiscaisParaMesmoExercicio(MetasFiscais selecionado) {
        String sql = " select * from metasfiscais mf " +
            " where mf.exercicio_id = :idExercicio " +
            " and mf.tipometasfiscais = '" + selecionado.getTipoMetasFiscais().name() + "'";
        if (selecionado.getId() != null) {
            sql += " and mf.id <> :idMetasFiscais";
        }
        Query consulta = getEntityManager().createNativeQuery(sql);
        consulta.setParameter("idExercicio", selecionado.getExercicio().getId());
        if (selecionado.getId() != null) {
            consulta.setParameter("idMetasFiscais", selecionado.getId());
        }
        if (!consulta.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    public Exercicio validaExercicioMetaFiscais(String exerc) {
        String sql = " select ex.* from EXERCICIO ex " +
            " where ex.ano = :exercicio ";
        Query consulta = em.createNativeQuery(sql, Exercicio.class);
        consulta.setParameter("exercicio", exerc);
        if (!consulta.getResultList().isEmpty()) {
            return (Exercicio) consulta.getSingleResult();
        } else {
            return new Exercicio();
        }

    }

    public List<Exercicio> listaExercicio() {
        Query consulta = em.createQuery("select distinct e from MetasFiscais n inner join n.exercicio e order by e.ano", Exercicio.class);
        return consulta.getResultList();
    }

    public MetasFiscais buscarMetasFiscaisPorExercicioELdo(MetasFiscais entity) {
        String sql = " from MetasFiscais where exercicio = :exercicio " +
            " and ldo = :ldo " +
            " and tipoMetasFiscais = :tipo ";
        if (entity.getId() != null) {
            sql += " and id <> :id ";
        }
        Query consulta = em.createQuery(sql, MetasFiscais.class);
        consulta.setParameter("exercicio", entity.getExercicio());
        consulta.setParameter("ldo", entity.getLdo());
        consulta.setParameter("tipo", entity.getTipoMetasFiscais());
        if (entity.getId() != null) {
            consulta.setParameter("id", entity.getId());
        }
        if (!consulta.getResultList().isEmpty()) {
            return (MetasFiscais) consulta.getSingleResult();
        }
        return null;
    }

    public List<MetasFiscais> buscarMetasFiscaisPorExercicio(Exercicio exercicio) {
        String sql = " select m.* from MetasFiscais m where m.exercicio_id = :exercicio ";

        Query consulta = em.createNativeQuery(sql, MetasFiscais.class);
        consulta.setParameter("exercicio", exercicio.getId());
        return (List<MetasFiscais>) consulta.getResultList();
    }

    public LDOFacade getLdoFacade() {
        return ldoFacade;
    }
}
