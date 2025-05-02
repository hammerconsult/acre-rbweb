/*
 * Codigo gerado automaticamente em Thu Apr 14 09:41:37 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.IndicadorProgramaPPA;
import br.com.webpublico.entidades.ProgramaPPA;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class IndicadorProgramaPPAFacade extends AbstractFacade<IndicadorProgramaPPA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IndicadorProgramaPPAFacade() {
        super(IndicadorProgramaPPA.class);
    }

    public List<IndicadorProgramaPPA> recuperaIndicadorProgramaPPa(ProgramaPPA p) {
        List<IndicadorProgramaPPA> recuperaInd = new ArrayList<IndicadorProgramaPPA>();
        String hql = "from IndicadorProgramaPPA p where p.programa = :prog";
        if (p != null) {
            Query q = em.createQuery(hql);
            q.setParameter("prog", p);
            recuperaInd = q.getResultList();
        } else {
            recuperaInd = new ArrayList<IndicadorProgramaPPA>();
        }
        return recuperaInd;
    }
}
