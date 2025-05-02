package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.TipoDocumentoServicoDeclarado;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TipoDocumentoServicoDeclaradoFacade extends AbstractFacade<TipoDocumentoServicoDeclarado> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TipoDocumentoServicoDeclaradoFacade() {
        super(TipoDocumentoServicoDeclarado.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
