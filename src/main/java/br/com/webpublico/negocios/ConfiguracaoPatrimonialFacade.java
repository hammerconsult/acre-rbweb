/*
 * Codigo gerado automaticamente em Thu Jun 16 13:52:10 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoPatrimonial;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class ConfiguracaoPatrimonialFacade extends AbstractFacade<ConfiguracaoPatrimonial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoPatrimonialFacade() {
        super(ConfiguracaoPatrimonial.class);
    }

    public ConfiguracaoPatrimonial listaConfiguracaoPatrimonialFiltrandoVigencia(Date desde) {
        //busca a maior vigencia, sendo menor ou igual ao filtro da tela
        try {
            String hql = " from ConfiguracaoPatrimonial conf "
                    + "  where conf.desde = (select max(conf2.desde) from ConfiguracaoPatrimonial conf2 "
                    + " where to_date(to_char(conf2.desde,'dd/MM/yyyy')) <= to_date(to_char(:parametro,'dd/MM/yyyy'))) ";
            Query q = em.createQuery(hql);
            q.setParameter("parametro", desde);
            q.setMaxResults(1);
            return (ConfiguracaoPatrimonial) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
