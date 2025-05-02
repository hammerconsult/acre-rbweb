/*
 * Codigo gerado automaticamente em Wed Apr 13 15:02:46 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Indicador;
import br.com.webpublico.entidades.ValorIndicador;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Stateless
public class ValorIndicadorFacade extends AbstractFacade<ValorIndicador> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ValorIndicadorFacade() {
        super(ValorIndicador.class);
    }

    public List<ValorIndicador> recuperaPorIndicador(Indicador indicador, Date inicio, Date fim) {
        String hql = "select a from ValorIndicador a where a.indicador = :indicador";
        hql += " and a.apurado";
        hql += " between :inicio and :fim";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("indicador", indicador);
        q.setParameter("inicio", inicio, TemporalType.DATE);
        q.setParameter("fim", fim, TemporalType.DATE);
        q.setMaxResults(50);
        return q.getResultList();

    }
}
