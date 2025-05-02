/*
 * Codigo gerado automaticamente em Tue Oct 25 09:24:54 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MedicaoSubAcaoPPA;
import br.com.webpublico.entidades.SubAcaoPPA;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class MedicaoSubAcaoPPAFacade extends AbstractFacade<MedicaoSubAcaoPPA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MedicaoSubAcaoPPAFacade() {
        super(MedicaoSubAcaoPPA.class);
    }

    public List<MedicaoSubAcaoPPA> recuperaMedicoes(SubAcaoPPA sub) {
        List<MedicaoSubAcaoPPA> recuperaMed = new ArrayList<MedicaoSubAcaoPPA>();
        String hql = "from MedicaoSubAcaoPPA m where m.subAcaoPPA = :sub";
        if (sub != null) {
            Query q = em.createQuery(hql);
            q.setParameter("sub", sub);
            recuperaMed = q.getResultList();
        } else {
            recuperaMed = new ArrayList<MedicaoSubAcaoPPA>();
        }
        return recuperaMed;
    }
}
