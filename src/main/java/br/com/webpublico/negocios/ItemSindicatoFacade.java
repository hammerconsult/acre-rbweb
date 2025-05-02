/*
 * Codigo gerado automaticamente em Tue Feb 07 15:19:14 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemSindicato;
import br.com.webpublico.entidades.Sindicato;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class ItemSindicatoFacade extends AbstractFacade<ItemSindicato> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemSindicatoFacade() {
        super(ItemSindicato.class);
    }

    public ItemSindicato recuperaItemSindicatoVigente(Sindicato sindicato) {
        try {

            String hql = " from ItemSindicato item where item.sindicato = :parametroSindicato "
                    + " and :dataVigencia >= item.inicioVigencia and "
                    + " :dataVigencia <= coalesce(item.finalVigencia,:dataVigencia)";
            Query q = em.createQuery(hql);
            q.setParameter("parametroSindicato", sindicato);
            q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
            q.setMaxResults(1);
            return (ItemSindicato) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
