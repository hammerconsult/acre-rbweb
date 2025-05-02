/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ParametrosLDO;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * @author Usuario
 */
@Stateless
public class ParametrosLDOFacade extends AbstractFacade<ParametrosLDO> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private LDOFacade ldoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametrosLDOFacade() {
        super(ParametrosLDO.class);
    }

    public ParametrosLDO retornaUltima() {
        String hql = "select pa.*, conf.desde from parametrosldo pa " +
            " inner join configuracaomodulo conf on pa.id = conf.id" +
            " order by conf.desde desc, conf.id desc";
        Query query = em.createNativeQuery(hql, ParametrosLDO.class);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty()) {
            return (ParametrosLDO) query.getSingleResult();
        }
        return new ParametrosLDO();
    }

    public ParametrosLDO listaFiltrandoVigencia(Date desde) {
        //busca a maior vigencia, sendo menor ou igual ao filtro da tela
        String hql = "select conf from ParametrosLDO conf "
            + " where conf.id = (select max(conf2.id) from ConfiguracaoModulo conf2 "
            + " where conf2.desde <= :parametro and conf2.id in "
            + " (select c.id from ParametrosLDO c))"
            + " order by conf.id desc ";

        Query q = em.createQuery(hql);
        q.setParameter("parametro", desde, TemporalType.DATE);
        q.setMaxResults(1);
        List<ParametrosLDO> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    public boolean verificarExisteParametrosLdoPorExercicioAndLDO(ParametrosLDO selecionado) {
        String sql = " select * from parametrosldo pldo " +
            " where pldo.exercicio_id = :idExercicio " +
            " and pldo.ldo_id = :idLdo ";
        if (selecionado.getId() != null) {
            sql += " and pldo.id <> :id";
        }
        Query consulta = getEntityManager().createNativeQuery(sql);
        consulta.setParameter("idExercicio", selecionado.getExercicio().getId());
        consulta.setParameter("idLdo", selecionado.getLdo().getId());
        if (selecionado.getId() != null) {
            consulta.setParameter("id", selecionado.getId());
        }
        return !consulta.getResultList().isEmpty();
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public LDOFacade getLdoFacade() {
        return ldoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
