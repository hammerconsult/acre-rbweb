/*
 * Codigo gerado automaticamente em Tue Jan 17 17:07:46 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemEntidadeConsignataria;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ItemEntidadeConsignatariaFacade extends AbstractFacade<ItemEntidadeConsignataria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemEntidadeConsignatariaFacade() {
        super(ItemEntidadeConsignataria.class);
    }

    /**
     * Retorna uma lista de ItemEntidadeConsignataria que estejam contidos
     * em um ItemEntidadeConsignataria que possua o mesmo
     * EventoFP e a vigência esteja dentro do período da
     * vigência de algum item ou algum item que esteja
     * dentro da vigência do item recebido por parâmetro
     *
     * @param item
     * @return
     */
    public List<ItemEntidadeConsignataria> listaItemComEventoFPComMesmaVigencia(ItemEntidadeConsignataria item) {
        Query q = em.createQuery(" select item from ItemEntidadeConsignataria item "
                + " inner join item.eventoFP evento "
                + " where evento = :parametroEvento and "
                + " ( (item.inicioVigencia >= :parametroInicio and item.finalVigencia <= :parametroFinal ) "
                + " or (item.inicioVigencia <= :parametroInicio and item.finalVigencia >= :parametroFinal) "
                + " or (item.finalVigencia >= :parametroInicio and item.inicioVigencia <= :parametroInicio ) "
                + " or (item.inicioVigencia <= :parametroFinal and item.finalVigencia >= :parametroFinal))");
        //+ " and item <> :parametroItem ");
        q.setParameter("parametroEvento", item.getEventoFP());
        q.setParameter("parametroInicio", item.getInicioVigencia());
        q.setParameter("parametroFinal", item.getFinalVigencia());

//        if (item.getId() != null) {
//            q.setParameter("parametroItem", item);
//        } else {
//            q.setParameter("parametroItem", null);
//        }

        return q.getResultList();
    }
}
