/*
 * Codigo gerado automaticamente em Fri Feb 11 08:09:57 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TarifaOutorga;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class TarifaOutorgaFacade extends AbstractFacade<TarifaOutorga> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TarifaOutorgaFacade() {
        super(TarifaOutorga.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<TarifaOutorga> listarTarifasVigentes() {
        String sql = "select tf.* from TarifaOutorga tf " +
            " where trunc(:dataAtual) between trunc(tf.inicioVigencia) and trunc(coalesce(tf.fimVigencia, :dataAtual))" +
            " order by tf.codigo";
        Query q = em.createNativeQuery(sql, TarifaOutorga.class);
        q.setParameter("dataAtual", new Date());
        return q.getResultList();
    }
}
