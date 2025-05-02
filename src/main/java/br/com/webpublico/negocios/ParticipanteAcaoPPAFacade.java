/*
 * Codigo gerado automaticamente em Tue Oct 25 08:57:56 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AcaoPPA;
import br.com.webpublico.entidades.ParticipanteAcaoPPA;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ParticipanteAcaoPPAFacade extends AbstractFacade<ParticipanteAcaoPPA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParticipanteAcaoPPAFacade() {
        super(ParticipanteAcaoPPA.class);
    }

    public List<ParticipanteAcaoPPA> recuperaParticipanteAcaoPPa(AcaoPPA ac) {
        List<ParticipanteAcaoPPA> recuperaParticipantes = new ArrayList<ParticipanteAcaoPPA>();
        String hql = "from ParticipanteAcaoPPA p where p.acaoPPA = :acao";
        if (ac != null) {
            Query q = em.createQuery(hql);
            q.setParameter("acao", ac);
            recuperaParticipantes = q.getResultList();
        }
        return recuperaParticipantes;
    }
}
