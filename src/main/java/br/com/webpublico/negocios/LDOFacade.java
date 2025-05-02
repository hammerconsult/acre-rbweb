/*
 * Codigo gerado automaticamente em Wed Apr 27 16:29:29 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class LDOFacade extends AbstractFacade<LDO> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CLPRealizadoFacade cLPRealizadoFacade;
    @EJB
    private ProvisaoPPAFacade provisaoPPAFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private ProdutoPPAFacade produtoPPAFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LDOFacade() {
        super(LDO.class);
    }

    @Override
    public LDO recuperar(Object id) {
        LDO l = em.find(LDO.class, id);
        l.getProvisaoPPALDOs().size();
        return l;
    }

    public List<LDO> listLDOExercicio(Exercicio exercicioCorrente) {
        String hql = "select l from LDO l where l.aprovacao is null and l.exercicio.id =:idExercicio";
        Query q = em.createQuery(hql);
        q.setParameter("idExercicio", exercicioCorrente.getId());
        return q.getResultList();
    }

    public List<LDO> listaLDOPorExercicio(Exercicio ex) {
        String hql = "select l from LDO l where l.exercicio =:paramEX";
        Query q = em.createQuery(hql);
        q.setParameter("paramEX", ex);
        return q.getResultList();
    }

    public LDO listaVigenteNoExercicio(Exercicio ex) {
        String hql = "select l from LDO l where l.exercicio =:param order by l.id desc";
        Query q = em.createQuery(hql);
        q.setParameter("param", ex);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (LDO) q.getSingleResult();
    }

    public LDO existeOutraLdoNoMesmoExercicio(LDO ldo) {
        String hql = "select l.* from LDO l where l.exercicio_id =:param";
        if (ldo.getId() != null) {
            hql += " and l.id <> :idLDO";
        }
        Query q = em.createNativeQuery(hql, LDO.class);
        q.setParameter("param", ldo.getExercicio().getId());
        q.setMaxResults(1);
        if (ldo.getId() != null) {
            q.setParameter("idLDO", ldo.getId());
        }
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (LDO) q.getSingleResult();
    }

    public LDO recuperaLdoPorPpa(PPA ppa) {
        String hql = "from LDO where ppa = :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", ppa);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (LDO) q.getSingleResult();
    }

    public List<LDO> listaLdoPorPpa(PPA ppa) {
        String sql = " SELECT L.* FROM LDO L "
            + " WHERE L.PPA_ID = :ppa_id ";
        Query consulta = em.createNativeQuery(sql, LDO.class);
        consulta.setParameter("ppa_id", ppa.getId());
        if (consulta.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return consulta.getResultList();
        }
    }

    public CLPRealizadoFacade getcLPRealizadoFacade() {
        return cLPRealizadoFacade;
    }

    public ProvisaoPPAFacade getProvisaoPPAFacade() {
        return provisaoPPAFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public PPAFacade getPpaFacade() {
        return ppaFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public SubProjetoAtividadeFacade getSubProjetoAtividadeFacade() {
        return subProjetoAtividadeFacade;
    }

    public ProdutoPPAFacade getProdutoPPAFacade() {
        return produtoPPAFacade;
    }

    public List<LDO> recuperarLdosComProdutoPPA(ProdutoPPA produtoPPA) {
        String sql = "select ldo.* from ldo " +
            "inner join PROVISAOPPALDO " +
            "on PROVISAOPPALDO.ldo_id = ldo.id " +
            "where produtoppa_id = :produto";
        Query consulta = em.createNativeQuery(sql, LDO.class);
        consulta.setParameter("produto", produtoPPA.getId());
        if (consulta.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return consulta.getResultList();
        }
    }

    public List<LDO> buscarLdosPorLoasEfetivadas(Exercicio exercicio) {
        Query q = em.createNativeQuery(" select distinct ldo.* from loa " +
            " inner join ldo on loa.ldo_id = ldo.id " +
            " where loa.efetivada = :efetivada " +
            " and ldo.exercicio_id = :exercicio", LDO.class);
        q.setParameter("efetivada", Boolean.TRUE);
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }
}
