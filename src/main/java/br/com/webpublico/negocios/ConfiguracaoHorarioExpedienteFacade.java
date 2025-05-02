/*
 * Codigo gerado automaticamente em Thu Oct 06 16:26:42 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoHorarioExpediente;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class ConfiguracaoHorarioExpedienteFacade extends AbstractFacade<ConfiguracaoHorarioExpediente> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoHorarioExpedienteFacade() {
        super(ConfiguracaoHorarioExpediente.class);
    }

    public ConfiguracaoHorarioExpediente listaFiltrandoVigencia(Date desde) {
        try {
            String hql = " from ConfiguracaoHorarioExpediente conf "
                    + "  where conf.desde = (select max(conf2.desde) from ConfiguracaoHorarioExpediente conf2 "
                    + " where to_date(to_char(conf2.desde,'dd/MM/yyyy')) <= to_date(to_char(:parametro,'dd/MM/yyyy'))) ";
            Query q = em.createQuery(hql);
            q.setParameter("parametro", desde);
            q.setMaxResults(1);
            return (ConfiguracaoHorarioExpediente) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


}
