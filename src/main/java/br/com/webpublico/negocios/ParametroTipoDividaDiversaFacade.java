/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemParametroTipoDividaDiv;
import br.com.webpublico.entidades.ParametroTipoDividaDiversa;
import br.com.webpublico.entidades.TipoDividaDiversa;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author claudio
 */
@Stateless
public class ParametroTipoDividaDiversaFacade extends AbstractFacade<ParametroTipoDividaDiversa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private TipoDividaDiversaFacade tipoDividaDiversaFacade;

    public TipoDividaDiversaFacade getTipoDividaDiversaFacade() {
        return tipoDividaDiversaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ParametroTipoDividaDiversaFacade() {
        super(ParametroTipoDividaDiversa.class);
    }

    @Override
    public ParametroTipoDividaDiversa recuperar(Object id) {
        ParametroTipoDividaDiversa af = em.find(ParametroTipoDividaDiversa.class, id);
        af.getItensParametro().size();
        return af;
    }

    @Override
    public void salvar(ParametroTipoDividaDiversa entity) {
        em.merge(entity);
    }

    @Override
    public List<ParametroTipoDividaDiversa> lista() {
        return em.createQuery("from ParametroTipoDividaDiversa").getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean existeCombinacaoIgualDoParametro(ParametroTipoDividaDiversa parametro) {
        String hql = "from ParametroTipoDividaDiversa p where p.exercicio = :exercicio "
                + "and p.tipoDividaDiversa = :tipoDividaDiversa";
        if (parametro.getId() != null) {
            hql += " and p.id <> :id ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", parametro.getExercicio());
        q.setParameter("tipoDividaDiversa", parametro.getTipoDividaDiversa());
        if (parametro.getId() != null) {
            q.setParameter("id", parametro.getId());
        }
        if (q.getResultList() == null || q.getResultList().size() == 0) {
            return false;
        }
        return true;
    }

    public ItemParametroTipoDividaDiv buscaItemParametroTipoDividaDiversa(Exercicio exercicio, TipoDividaDiversa tipoDividaDiversa, BigDecimal valor) {
        ItemParametroTipoDividaDiv retorno = null;
        String sql = " select iptdd "
                + "    from ParametroTipoDividaDiversa ptdd "
                + "   inner join ptdd.itensParametro iptdd "
                + " where ptdd.exercicio = :exercicio "
                + "   and ptdd.tipoDividaDiversa = :tipodividadiversa "
                + "   and (iptdd.valorInicial <= :valor and iptdd.valorFinal >= :valor) ";
        Query q = em.createQuery(sql);
        q.setParameter("exercicio", exercicio);
        q.setParameter("tipodividadiversa", tipoDividaDiversa);
        q.setParameter("valor", valor);
        try {
            retorno = (ItemParametroTipoDividaDiv) q.getSingleResult();
        } catch (NoResultException ex) {
            retorno = null;
        }
        return retorno;
    }

    public List<ParametroTipoDividaDiversa> buscarParametroTipoDividaDiversaPorExercicio(Exercicio exercicio) {
        Query q = em.createNativeQuery("select * from ParametroTipoDividaDiversa where exercicio_id = :idExercicio", ParametroTipoDividaDiversa.class);
        q.setParameter("idExercicio", exercicio.getId());
        return q.getResultList();
    }
}
