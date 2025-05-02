package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.ConviteUsuarioNfse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ConviteUsuarioNfseFacade extends AbstractFacade<ConviteUsuarioNfse> {

    private static final Logger logger = LoggerFactory.getLogger(ConviteUsuarioNfseFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConviteUsuarioNfseFacade() {
        super(ConviteUsuarioNfse.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
