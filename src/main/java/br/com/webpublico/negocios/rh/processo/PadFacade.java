package br.com.webpublico.negocios.rh.processo;


import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.processo.Pad;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless

public class PadFacade extends AbstractFacade<Pad> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public PadFacade() {
        super(Pad.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasProcessoAdministrativoDisciplonarEmCurso(VinculoFP vinculoFP) {
        String sql = "select * from pad where penalidade = :ativo and contratofp_id = :idContrato";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ativo", true);
        q.setParameter("idContrato", vinculoFP.getId());
        return !q.getResultList().isEmpty();
    }
}
