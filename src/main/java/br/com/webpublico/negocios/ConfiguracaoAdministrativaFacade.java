/*
 * Codigo gerado automaticamente em Fri Feb 10 11:17:13 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoAdministrativa;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ConfiguracaoAdministrativaFacade extends AbstractFacade<ConfiguracaoAdministrativa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoAdministrativaFacade() {
        super(ConfiguracaoAdministrativa.class);
    }

    public ConfiguracaoAdministrativa verificarParametroJaExistente(ConfiguracaoAdministrativa confAdmParam) {
        String hql = "from ConfiguracaoAdministrativa ca"
                + " where ca.parametro = :parametro";

        Query q = em.createQuery(hql);
        q.setParameter("parametro", confAdmParam.getParametro());
        q.setMaxResults(1);

        try {
            return (ConfiguracaoAdministrativa) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ConfiguracaoAdministrativa> lista() {
        String hql = "from ConfiguracaoAdministrativa ca"
                + " where ca.validoAte is null";

        Query q = em.createQuery(hql);
        try {
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<ConfiguracaoAdministrativa>();
        }
    }

    public List<ConfiguracaoAdministrativa> recuperarHistoricoAlteracoes(ConfiguracaoAdministrativa confAdm) {
        String hql = "from ConfiguracaoAdministrativa as ca where ca.parametro = :parametro order by ca.desde desc";

        Query q = em.createQuery(hql);
        q.setParameter("parametro", confAdm.getParametro());
        try {
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<ConfiguracaoAdministrativa>();
        }
    }

    public ConfiguracaoAdministrativa recuperaParametro(String parametro) {
        String hql = "from ConfiguracaoAdministrativa as ca where ca.parametro = :parametro and ca.validoAte is null";

        Query q = em.createQuery(hql);
        q.setParameter("parametro", parametro);
        q.setMaxResults(1);
        try {
            return (ConfiguracaoAdministrativa) q.getSingleResult();
        } catch (Exception e) {
            return new ConfiguracaoAdministrativa();
        }
    }

    public boolean validarSeParametroPossuiVinculoComLocalEstoque() {
        String hql = "select local from LocalEstoque local";
        Query consulta = em.createQuery(hql);
        try {
            if (!consulta.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
