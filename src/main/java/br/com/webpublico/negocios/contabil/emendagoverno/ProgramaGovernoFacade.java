package br.com.webpublico.negocios.contabil.emendagoverno;

import br.com.webpublico.entidades.contabil.emendagoverno.ProgramaGoverno;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ProgramaGovernoFacade extends AbstractFacade<ProgramaGoverno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ProgramaGovernoFacade() {
        super(ProgramaGoverno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
