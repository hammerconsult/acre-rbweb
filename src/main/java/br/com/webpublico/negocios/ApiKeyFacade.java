package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ApiKey;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Stateless
public class ApiKeyFacade extends AbstractFacade<ApiKey> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ApiKeyFacade() {
        super(ApiKey.class);
    }

    @Override
    public void preSave(ApiKey entidade) {
        if (entidade.getId() == null) {
            entidade.setApiKey(UUID.randomUUID().toString());
        }
    }
}
