/*
 * Codigo gerado automaticamente em Tue Dec 06 10:05:41 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FormaCobranca;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Stateless
public class FormaCobrancaFacade extends AbstractFacade<FormaCobranca> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FormaCobrancaFacade() {
        super(FormaCobranca.class);
    }

    public List<FormaCobranca> listaPorPeriodo(Date periodoInicial, Date periodoFinal) {
        StringBuilder sb = new StringBuilder("from FormaCobranca fc");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String junct = " where ";
        if (periodoInicial != null) {
            sb.append(junct + "fc.inicioVigencia >= :dataInicial");
            junct = " and ";
        }
        if (periodoFinal != null) {
            sb.append(junct + "fc.finalVigencia <= :dataFinal");
        }
        Query q = em.createQuery(sb.toString());
        if (periodoInicial != null) {
            q.setParameter("dataInicial", /*formatterDiaMesAno.format(*/periodoInicial/*)*/);
        }
        if (periodoFinal != null) {
            q.setParameter("dataFinal", /*formatterDiaMesAno.format(*/periodoFinal/*)*/);
        }
        q.setMaxResults(50);
        return q.getResultList();
    }

    public boolean emUso(FormaCobranca formaCobranca) {
        Query q = em.createNativeQuery("SELECT count(d.id) FROM Divida d " +
                " INNER JOIN formacobrancadivida fcd ON fcd.divida_id = d.id AND fcd.formacobranca_id = :formaID");
        q.setParameter("formaID", formaCobranca.getId());
        BigDecimal i = (BigDecimal) q.getSingleResult();
        return i.compareTo(BigDecimal.ZERO) > 0;
    }
}
