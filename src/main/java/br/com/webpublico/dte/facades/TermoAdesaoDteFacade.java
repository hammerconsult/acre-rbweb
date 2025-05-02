package br.com.webpublico.dte.facades;

import br.com.webpublico.dte.entidades.ModeloDocumentoDte;
import br.com.webpublico.dte.entidades.TermoAdesaoDte;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TermoAdesaoDteFacade extends AbstractFacade<TermoAdesaoDte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TermoAdesaoDteFacade() {
        super(TermoAdesaoDte.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
