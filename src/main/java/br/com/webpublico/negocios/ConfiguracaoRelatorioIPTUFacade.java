/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoRelatorioIPTU;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ConfiguracaoRelatorioIPTUFacade extends AbstractFacade<ConfiguracaoRelatorioIPTU> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConfiguracaoRelatorioIPTUFacade() {
        super(ConfiguracaoRelatorioIPTU.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ConfiguracaoRelatorioIPTU recuperar(Object id) {
        ConfiguracaoRelatorioIPTU conf = em.find(ConfiguracaoRelatorioIPTU.class, id);
        conf.getEventos().size();
        return conf;    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ConfiguracaoRelatorioIPTU recuperaUltimo() {
        Query query = em.createQuery(" select  c from ConfiguracaoRelatorioIPTU c ").setMaxResults(1);
        if (!query.getResultList().isEmpty()) {
            ConfiguracaoRelatorioIPTU conf =(ConfiguracaoRelatorioIPTU) query.getResultList().get(0);
            conf.getEventos().size();
            return conf;
        }
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
