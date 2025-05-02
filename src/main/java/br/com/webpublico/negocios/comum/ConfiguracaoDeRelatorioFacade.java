package br.com.webpublico.negocios.comum;


import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by renato on 17/07/19.
 */
@Stateless
public class ConfiguracaoDeRelatorioFacade extends AbstractFacade<ConfiguracaoDeRelatorio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public ConfiguracaoDeRelatorioFacade() {
        super(ConfiguracaoDeRelatorio.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoDeRelatorio getConfiguracaoPorChave() {
        List<ConfiguracaoDeRelatorio> lista = lista();
        if (lista != null && !lista.isEmpty()) {
            for (ConfiguracaoDeRelatorio configuracaoDeRelatorio : lista) {
                if (configuracaoDeRelatorio.getChave().equals(getUsuarioBanco())) {
                    return configuracaoDeRelatorio;
                }
            }
        }
        return null;
    }

    public String getUsuarioBanco() {
        return sistemaFacade.getUsuarioBancoDeDados();
    }
}
