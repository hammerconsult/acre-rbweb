/*
 * Codigo gerado automaticamente em Tue Oct 25 10:44:27 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ProgramaPPA;
import br.com.webpublico.entidades.PublicoAlvoProgramaPPA;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class PublicoAlvoProgramaPPAFacade extends AbstractFacade<PublicoAlvoProgramaPPA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PublicoAlvoProgramaPPAFacade() {
        super(PublicoAlvoProgramaPPA.class);
    }

    public List<PublicoAlvoProgramaPPA> recuperaPublicoAlvo(ProgramaPPA prog) {
        List<PublicoAlvoProgramaPPA> recuperaPub = new ArrayList<PublicoAlvoProgramaPPA>();
        String hql = "from PublicoAlvoProgramaPPA p where p.programaPPA = :prog";
        if (prog != null) {
            Query q = em.createQuery(hql);
            q.setParameter("prog", prog);
            recuperaPub = q.getResultList();
        } else {
            recuperaPub = new ArrayList<PublicoAlvoProgramaPPA>();
        }
        return recuperaPub;
    }
}
