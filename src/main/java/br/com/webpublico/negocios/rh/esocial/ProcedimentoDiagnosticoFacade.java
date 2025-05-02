package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.ProcedimentoDiagnostico;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ProcedimentoDiagnosticoFacade extends AbstractFacade<ProcedimentoDiagnostico> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ProcedimentoDiagnosticoFacade() {
        super(ProcedimentoDiagnostico.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
