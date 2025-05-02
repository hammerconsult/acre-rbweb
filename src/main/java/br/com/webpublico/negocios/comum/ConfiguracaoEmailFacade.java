package br.com.webpublico.negocios.comum;

import br.com.webpublico.entidades.ConfiguracaoEmail;
import br.com.webpublico.negocios.AbstractFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ConfiguracaoEmailFacade extends AbstractFacade<ConfiguracaoEmail> {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguracaoEmailFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConfiguracaoEmailFacade() {
        super(ConfiguracaoEmail.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Boolean hasConfiguracaoCadastrada() {
        return super.count() > 0;
    }

    public ConfiguracaoEmail recuperarUtilmo() {
        String hql = "from ConfiguracaoEmail obj order by obj.id desc";
        Query q = getEntityManager().createQuery(hql);
        q.setMaxResults(1);
        return (ConfiguracaoEmail) q.getSingleResult();
    }

    @Override
    public void salvar(ConfiguracaoEmail entity) {
        super.salvar(entity);
    }
}
