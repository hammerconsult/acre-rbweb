package br.com.webpublico.negocios.comum.dashboard;

import br.com.webpublico.entidades.comum.dashboard.ConfiguracaoDeDashboard;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by renato on 14/08/19.
 */
@Stateless
public class ConfiguracaoDeDashboardFacade extends AbstractFacade<ConfiguracaoDeDashboard> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public ConfiguracaoDeDashboardFacade() {
        super(ConfiguracaoDeDashboard.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoDeDashboard getConfiguracaoPorChave(){
        List<ConfiguracaoDeDashboard> lista = lista();
        if(lista != null && !lista.isEmpty()){
            for (ConfiguracaoDeDashboard config : lista) {
                if(config.getChave().equals(getUsuarioBanco())) {
                    return config;
                }
            }
        }
        return null;
    }

    public String getUsuarioBanco(){
        return sistemaFacade.getUsuarioBancoDeDados();
    }
}
