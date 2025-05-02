/*
 * Codigo gerado automaticamente em Mon Sep 05 09:56:38 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EnquadramentoPCS;
import br.com.webpublico.entidades.ReajusteEnquadramentoPCS;
import br.com.webpublico.entidades.ReajustePCS;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ReajustePCSFacade extends AbstractFacade<ReajustePCS> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReajustePCSFacade() {
        super(ReajustePCS.class);
    }

    @Override
    public ReajustePCS recuperar(Object id) {
        Query q = em.createQuery("select r from ReajustePCS r inner join r.reajusteEnquadramentoPCS enq where r.id = :id order by enq.enquadramentoPCS.categoriaPCS.descricao, enq.enquadramentoPCS.progressaoPCS.descricao");
        q.setParameter("id", id);
        if (!q.getResultList().isEmpty()) {
            ReajustePCS a = (ReajustePCS) q.getResultList().get(0);
            Query q2 = em.createQuery("select itens from ReajusteEnquadramentoPCS itens where itens.reajustePCS.id = :id order by itens.enquadramentoPCS.categoriaPCS.descricao, itens.enquadramentoPCS.progressaoPCS.descricao");
            q2.setParameter("id", id);
            a.setReajusteEnquadramentoPCS(q2.getResultList());
            return a;
        }

        return null;
    }

    @Override
    public void remover(ReajustePCS entity) {
        entity = recuperar(entity.getId());
        List<EnquadramentoPCS> enquadramentos = Lists.newArrayList();
        for (ReajusteEnquadramentoPCS reajusteEnquadramentoPC : entity.getReajusteEnquadramentoPCS()) {
            enquadramentos.add(reajusteEnquadramentoPC.getEnquadramentoPCS());
            em.remove(reajusteEnquadramentoPC);
        }
        super.remover(entity);

    }


}
