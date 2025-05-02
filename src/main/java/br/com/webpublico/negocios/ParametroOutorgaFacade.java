package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ParametrosOutorgaRBTrans;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 26/08/14
 * Time: 09:16
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ParametroOutorgaFacade extends AbstractFacade<ParametrosOutorgaRBTrans> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private IndiceEconomicoFacade indiceFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ParametroOutorgaFacade() {
        super(ParametrosOutorgaRBTrans.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }


    public IndiceEconomicoFacade getIndiceFacade() {
        return indiceFacade;
    }

    public ParametrosOutorgaRBTrans getParametroOutorgaRBTransVigente() {
        String hql = "from ParametrosOutorgaRBTrans parametro where parametro.exercicio = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", sistemaFacade.getExercicioCorrente());
        q.setMaxResults(1);
        try {
            ParametrosOutorgaRBTrans parametro = (ParametrosOutorgaRBTrans) q.getSingleResult();
            parametro.getParametroOutorgaSubvencao().size();
            return parametro;
        } catch (NoResultException nre) {
            return null;
        }
    }


    @Override
    public ParametrosOutorgaRBTrans recuperar(Object id) {
        ParametrosOutorgaRBTrans parametro = em.find(ParametrosOutorgaRBTrans.class, id);
        parametro.getParametroOutorgaSubvencao().size();
        return parametro;
    }

    public ParametrosOutorgaRBTrans recuperarParametroSubvencao(Exercicio exercicio) {
        String hql = "select param from ParametrosOutorgaRBTrans param where param.exercicio = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        if (!q.getResultList().isEmpty()) {
            ParametrosOutorgaRBTrans parametro = (ParametrosOutorgaRBTrans) q.getResultList().get(0);
            parametro.getParametroOutorgaSubvencao().size();
            return parametro;
        }
        return null;
    }

    public boolean hasParametroOutorgaNoExercicio(Exercicio exercicio) {
        String sql = "select * from PARAMOUTORGARBTRANS parametro where parametro.exercicio_id = :exercicio";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        return !q.getResultList().isEmpty();
    }
}
