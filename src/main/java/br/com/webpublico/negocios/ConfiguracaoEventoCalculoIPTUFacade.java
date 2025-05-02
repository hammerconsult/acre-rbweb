/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoEventoIPTU;
import com.google.common.collect.Lists;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ConfiguracaoEventoCalculoIPTUFacade extends AbstractFacade<ConfiguracaoEventoIPTU> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoCalculoFacade eventoCalculoFacade;


    public ConfiguracaoEventoCalculoIPTUFacade() {
        super(ConfiguracaoEventoIPTU.class);
    }

    public EventoCalculoFacade getEventoCalculoFacade() {
        return eventoCalculoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ConfiguracaoEventoIPTU recuperar(Object id) {
        ConfiguracaoEventoIPTU configuracaoEventoIPTU = em.find(ConfiguracaoEventoIPTU.class,id);
        configuracaoEventoIPTU.getEventos().size();
        return configuracaoEventoIPTU;    //To change body of overridden methods use File | Settings | File Templates.
    }

    public List<ConfiguracaoEventoIPTU> listarAtivos() {
        List<ConfiguracaoEventoIPTU> lista = lista();
        List<ConfiguracaoEventoIPTU> ativos = Lists.newArrayList();
        for (ConfiguracaoEventoIPTU configuracaoEventoIPTU : lista) {
            if (configuracaoEventoIPTU.getAtivo()) {
                ativos.add(configuracaoEventoIPTU);
            }
        }
        return ativos;
    }
}
