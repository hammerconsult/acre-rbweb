/*
 * Codigo gerado automaticamente em Wed Aug 24 10:34:19 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ConfiguracaoProtocolo;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Stateless
public class ConfiguracaoProtocoloFacade extends AbstractFacade<ConfiguracaoProtocolo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoProtocoloFacade() {
        super(ConfiguracaoProtocolo.class);
    }

    public ConfiguracaoProtocolo retornaUltima() {
        String hql = "select a from ConfiguracaoProtocolo a where a.desde = (select max(b.desde) from ConfiguracaoProtocolo b)";
        Query query = em.createQuery(hql);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty()) {

            return (ConfiguracaoProtocolo) query.getSingleResult();

        }

        return null;
    }

    public ConfiguracaoProtocolo listaConfiguracaoProtocoloFiltrandoVigencia(Date desde) {
        //busca a maior vigencia, sendo menor ou igual ao filtro da tela
        String hql = "select conf from ConfiguracaoProtocolo conf "
                + " where conf.id = (select max(conf2.id) from ConfiguracaoModulo conf2 "
                + " where conf2.desde <= :parametro and conf2.id in "
                + " (select c.id from ConfiguracaoProtocolo c))"
                + " order by id desc ";

        Query q = em.createQuery(hql);
        q.setParameter("parametro", desde, TemporalType.TIMESTAMP);
        q.setMaxResults(1);
        List<ConfiguracaoProtocolo> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return new ConfiguracaoProtocolo();
        }
        return lista.get(0);
    }
}
