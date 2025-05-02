package br.com.webpublico.negocios.rh.configuracao;

import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoAposentadoria;
import br.com.webpublico.entidades.RegraAposentadoria;
import br.com.webpublico.negocios.AbstractFacade;
import com.google.common.base.Preconditions;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @Author peixe on 05/11/2015  18:00.
 */
@Stateless
public class ConfiguracaoAposentadoriaFacade extends AbstractFacade<ConfiguracaoAposentadoria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConfiguracaoAposentadoriaFacade() {
        super(ConfiguracaoAposentadoria.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ConfiguracaoAposentadoria recuperar(Object id) {
        ConfiguracaoAposentadoria config = em.find(ConfiguracaoAposentadoria.class, id);
        config.getTempoMinimoAposentadorias().size();
        return config;
    }

    public ConfiguracaoAposentadoria buscarConfiguracaoPorRegraAposentadoria(RegraAposentadoria regra) {
        Preconditions.checkNotNull(regra);
        Query q = em.createQuery("select apo from ConfiguracaoAposentadoria apo where apo.regraAposentadoria = :regra");
        q.setParameter("regra", regra);
        try {
            ConfiguracaoAposentadoria conf = (ConfiguracaoAposentadoria) q.getSingleResult();
            conf.getTempoMinimoAposentadorias().size();
            return conf;
        } catch (NoResultException noResult) {
            logger.debug("Nenhum resultado encontrado para " + regra);
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
