/*
 * Codigo gerado automaticamente em Tue Jan 10 14:33:58 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.RecolhimentoSEFIP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class RecolhimentoSEFIPFacade extends AbstractFacade<RecolhimentoSEFIP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RecolhimentoSEFIPFacade() {
        super(RecolhimentoSEFIP.class);
    }

    public boolean existeCodigo(RecolhimentoSEFIP recolhimentoSEFIP) {
        String hql = " from RecolhimentoSEFIP r where r.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", recolhimentoSEFIP.getCodigo());

        List<RecolhimentoSEFIP> lista = new ArrayList<RecolhimentoSEFIP>();
        lista = q.getResultList();

        if (lista.contains(recolhimentoSEFIP)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }
}
