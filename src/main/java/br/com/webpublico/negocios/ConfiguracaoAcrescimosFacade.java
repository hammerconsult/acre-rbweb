/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoAcrescimos;
import br.com.webpublico.entidades.MultaConfiguracaoAcrescimo;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author gustavo
 */
@Stateless
public class ConfiguracaoAcrescimosFacade extends AbstractFacade<ConfiguracaoAcrescimos> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoAcrescimosFacade() {
        super(ConfiguracaoAcrescimos.class);
    }

    @Override
    public void salvar(ConfiguracaoAcrescimos entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(ConfiguracaoAcrescimos entity) {
        super.salvarNovo(entity);
    }

    @Override
    public ConfiguracaoAcrescimos recuperar(Object id) {
        ConfiguracaoAcrescimos ca = em.find(ConfiguracaoAcrescimos.class, id);
        inicializarDependencias(ca);
        return ca;
    }

    private void inicializarDependencias(ConfiguracaoAcrescimos ca) {
        if (ca != null) {
            for (MultaConfiguracaoAcrescimo multa : ca.getMultas()) {
                Hibernate.initialize(multa.getDiasAtraso());
                Hibernate.initialize(multa.getIncidencias());
            }
            Hibernate.initialize(ca.getIncidencias());
            Hibernate.initialize(ca.getHonorarios());
            Hibernate.initialize(ca.getNaturezasIsencao());
        }
    }

    public List<ConfiguracaoAcrescimos> recuperarTodasComDependencias() {
        List<ConfiguracaoAcrescimos> lista = lista();
        for (ConfiguracaoAcrescimos configuracaoAcrescimos : lista) {
            inicializarDependencias(configuracaoAcrescimos);
        }
        return lista;
    }
}
