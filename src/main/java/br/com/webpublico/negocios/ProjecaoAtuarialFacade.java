/*
 * Codigo gerado automaticamente em Thu Jul 05 13:48:54 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ProjecaoAtuarial;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;

@Stateless
public class ProjecaoAtuarialFacade extends AbstractFacade<ProjecaoAtuarial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;

    public ProjecaoAtuarialFacade() {
        super(ProjecaoAtuarial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ProjecaoAtuarial recuperar(Object id) {
        ProjecaoAtuarial cd = em.find(ProjecaoAtuarial.class, id);
        cd.getItemProjecaoAtuarials().size();
        return cd;
    }

    public Integer retornaMenorData(Exercicio ex) {
        String sql = "SELECT IPA.EXERCICIO "
                + " FROM ITEMPROJECAOATUARIAL IPA "
                + " INNER JOIN PROJECAOATUARIAL PA ON IPA.PROJECAOATUARIAL_ID = PA.ID "
                + " INNER JOIN exercicio exerc ON ipa.exercicio = exerc.ano "
                + " WHERE exerc.ID = :param "
                + " GROUP BY ipa.exercicio ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("param", ex.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getSingleResult()).intValue();
        } else {
            return 0;
        }
    }

    public String retornaNotasProjecao(Exercicio ex) {
        String toReturn = "";
        String sql = " select pa.* from ProjecaoAtuarial pa "
                + " where pa.exercicio_id = :exercicio and pa.dataAvaliacao = (select max(dataAvaliacao) from ProjecaoAtuarial where pa.exercicio_id = :exercicio) ";
        Query q = em.createNativeQuery(sql, ProjecaoAtuarial.class);
        q.setParameter("exercicio", ex);
        if (!q.getResultList().isEmpty()) {
            toReturn = ((ProjecaoAtuarial) q.getSingleResult()).getHipoteses();
        }
        return toReturn;
    }

    public Integer retornaMaiorData(Exercicio ex) {
        String sql = " SELECT MAX(IPA.EXERCICIO) "
                + " FROM ITEMPROJECAOATUARIAL IPA "
                + " INNER JOIN PROJECAOATUARIAL PA ON IPA.PROJECAOATUARIAL_ID = PA.ID "
                + " INNER JOIN EXERCICIO exerc on pa.EXERCICIO_ID = EXERC.id "
                + " WHERE EXERC.ID = :param ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("param", ex.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty() && q.getSingleResult() != null) {
            return ((BigDecimal) q.getSingleResult()).intValue();
        } else {
            return 0;
        }
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ProjecaoAtuarial buscaUltimaProjecao() {
        String sql = "SELECT p.* FROM ProjecaoAtuarial p WHERE p.dataAvaliacao = (SELECT min(p2.dataAvaliacao) FROM ProjecaoAtuarial p2)";
        Query q = em.createNativeQuery(sql, ProjecaoAtuarial.class);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            ProjecaoAtuarial pro = (ProjecaoAtuarial) q.getSingleResult();
            pro.getItemProjecaoAtuarials().size();
            return pro;
        }
        return null;
    }
}
