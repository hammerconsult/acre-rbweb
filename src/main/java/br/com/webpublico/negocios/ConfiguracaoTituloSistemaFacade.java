package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoPerfilApp;
import br.com.webpublico.entidades.ConfiguracaoTituloSistema;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 16/05/14
 * Time: 08:21
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ConfiguracaoTituloSistemaFacade extends AbstractFacade<ConfiguracaoTituloSistema> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public ConfiguracaoTituloSistemaFacade() {
        super(ConfiguracaoTituloSistema.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoTituloSistema recuperarTitulos() {
        Query q = em.createQuery("select configuracao from ConfiguracaoTituloSistema configuracao");
        if (q.getResultList().isEmpty()) {
            return new ConfiguracaoTituloSistema();
        }
        return (ConfiguracaoTituloSistema) q.getResultList().get(0);
    }

    public ConfiguracaoPerfilApp recuperaConfiguracaoPerfilApp() {
        Query q = em.createQuery("select configuracao from ConfiguracaoPerfilApp configuracao");
        if (q.getResultList().isEmpty()) {
            return new ConfiguracaoPerfilApp();
        }
        return (ConfiguracaoPerfilApp) q.getResultList().get(0);
    }

    public void salvar(ConfiguracaoPerfilApp configuracaoPerfilApp) {
        em.merge(configuracaoPerfilApp);
    }
}
