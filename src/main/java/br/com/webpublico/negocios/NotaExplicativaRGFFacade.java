package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.NotaExplicativaRGF;
import br.com.webpublico.enums.AnexoRGF;
import br.com.webpublico.enums.EsferaDoPoder;
import br.com.webpublico.enums.Mes;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class NotaExplicativaRGFFacade extends AbstractFacade<NotaExplicativaRGF> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public NotaExplicativaRGFFacade() {
        super(NotaExplicativaRGF.class);
    }

    public NotaExplicativaRGF buscarNotaPorMes(Mes mes, Exercicio exercicio, AnexoRGF anexoRGF) {
        String sql = " select * from NotaExplicativaRGF nota " +
            " where nota.mes = :mes " +
            "   and nota.anexoRGF = :anexoRGF " +
            "   and nota.esferaDoPoder is null " +
            "   and nota.exercicio_id = :exercicio ";
        Query q = em.createNativeQuery(sql, NotaExplicativaRGF.class);
        q.setParameter("mes", mes.name());
        q.setParameter("anexoRGF", anexoRGF.name());
        q.setParameter("exercicio", exercicio.getId());
        try {
            List resultList = q.getResultList();
            return resultList.isEmpty() ? new NotaExplicativaRGF(mes) : (NotaExplicativaRGF) resultList.get(0);
        } catch (NoResultException nre) {
            return new NotaExplicativaRGF(mes);
        }
    }

    public NotaExplicativaRGF buscarNotaPorMesAndEsferaDoPoder(Mes mes, Exercicio exercicio, AnexoRGF anexoRGF, EsferaDoPoder esferaDoPoder) {
        String sql = " select * from NotaExplicativaRGF nota " +
            " where nota.mes = :mes " +
            "   and nota.anexoRGF = :anexoRGF " +
            "   and nota.esferaDoPoder = :esferaDoPoder " +
            "   and nota.exercicio_id = :exercicio ";
        Query q = em.createNativeQuery(sql, NotaExplicativaRGF.class);
        q.setParameter("mes", mes.name());
        q.setParameter("anexoRGF", anexoRGF.name());
        q.setParameter("esferaDoPoder", esferaDoPoder.name());
        q.setParameter("exercicio", exercicio.getId());
        try {
            return (NotaExplicativaRGF) q.getSingleResult();
        } catch (NoResultException nre) {
            return new NotaExplicativaRGF(mes);
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
