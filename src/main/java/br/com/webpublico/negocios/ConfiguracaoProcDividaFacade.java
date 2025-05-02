/*
 * Codigo gerado automaticamente em Tue Apr 17 16:07:17 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoProcDivida;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class ConfiguracaoProcDividaFacade extends AbstractFacade<ConfiguracaoProcDivida> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CargoFacade cargoFacade;

    public ConfiguracaoProcDivida recuperaConfiguracaoProcDividaFiltrandoVigencia(Date desde) {
        //busca a maior vigencia, sendo menor ou igual ao filtro da tela
        try {
            String hql = " from ConfiguracaoProcDivida conf "
                    + " where conf.id = (select max(conf2.id) from ConfiguracaoProcDivida conf2 "
                    + " where to_date(to_char(:parametro,'dd/MM/yyyy')) >= to_date(to_char(conf2.inicioVigencia,'dd/MM/yyyy')) "
                    + " and  to_date(to_char(:parametro,'dd/MM/yyyy')) <= to_date(to_char(coalesce(conf2.fimVigencia, :parametro),'dd/MM/yyyy')) ) ";
            Query q = em.createQuery(hql);
            q.setParameter("parametro", desde);
            q.setMaxResults(1);
            return (ConfiguracaoProcDivida) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


    public ConfiguracaoProcDivida recuperaUltima() {
        String query = "from ConfiguracaoProcDivida a where a.fimVigencia is null";
        Query q = em.createQuery(query);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return ((ConfiguracaoProcDivida) q.getSingleResult());
        }
        return null;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoProcDividaFacade() {
        super(ConfiguracaoProcDivida.class);
    }

    public CargoFacade getCargoFacade() {
        return cargoFacade;
    }
}
