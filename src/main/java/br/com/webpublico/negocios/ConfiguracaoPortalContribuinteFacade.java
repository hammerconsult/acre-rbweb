/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoPortalContribuinte;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless
public class ConfiguracaoPortalContribuinteFacade extends AbstractFacade<ConfiguracaoPortalContribuinte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    public ConfiguracaoPortalContribuinteFacade() {
        super(ConfiguracaoPortalContribuinte.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public ConfiguracaoPortalContribuinte buscarUtilmo() {
        try {
            ConfiguracaoPortalContribuinte singleResult = (ConfiguracaoPortalContribuinte) em.createQuery("select ultimo from ConfiguracaoPortalContribuinte ultimo").setMaxResults(1).getSingleResult();
            Hibernate.initialize(singleResult.getDocumentosObrigatorios());
            Hibernate.initialize(singleResult.getPermissoesAprovacaoCredores());
            Hibernate.initialize(singleResult.getHtmls());
            return singleResult;
        } catch (NoResultException e) {
            ConfiguracaoPortalContribuinte merge = em.merge(new ConfiguracaoPortalContribuinte());
            Hibernate.initialize(merge.getDocumentosObrigatorios());
            return merge;
        }
    }

}
