/*
 * Codigo gerado automaticamente em Fri Feb 11 08:42:00 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoDAM;
import br.com.webpublico.entidades.ConfiguracaoPix;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ConfiguracaoDAMFacade extends AbstractFacade<ConfiguracaoDAM> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private TributoFacade tributoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoDAMFacade() {
        super(ConfiguracaoDAM.class);
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    public boolean existeCondiguracaoDAMComEstaDescricao(ConfiguracaoDAM configuracao) {
        String hql = "select c from ConfiguracaoDAM c where lower(c.descricao) = :descricao";
        if (configuracao.getId() != null) {
            hql += " and c != :configuracao";
        }
        Query q = em.createQuery(hql);
        q.setParameter("descricao", configuracao.getDescricao().toLowerCase().trim());
        if (configuracao.getId() != null) {
            q.setParameter("configuracao", configuracao);
        }
        return !q.getResultList().isEmpty();
    }

    public ConfiguracaoDAM buscarConfiguracaoPeloCodigoFebraban(String codigoFebraban) {
        Query q = em.createQuery("from ConfiguracaoDAM where codigoFebraban = :codigoFebraban");
        q.setParameter("codigoFebraban", Integer.parseInt(codigoFebraban));
        q.setMaxResults(1);
        List<ConfiguracaoDAM> retorno = q.getResultList();
        if (retorno.isEmpty()) {
            return null;
        }
        return retorno.get(0);
    }

}
