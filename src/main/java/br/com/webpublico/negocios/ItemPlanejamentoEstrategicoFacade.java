/*
 * Codigo gerado automaticamente em Thu Mar 31 17:24:24 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemPlanejamentoEstrategico;
import br.com.webpublico.entidades.MacroObjetivoEstrategico;
import br.com.webpublico.entidades.PlanejamentoEstrategico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ItemPlanejamentoEstrategicoFacade extends AbstractFacade<ItemPlanejamentoEstrategico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemPlanejamentoEstrategicoFacade() {
        super(ItemPlanejamentoEstrategico.class);
    }

    public List<ItemPlanejamentoEstrategico> listaFiltrando(String s, PlanejamentoEstrategico p, String... atributos) {
        String hql = "from ItemPlanejamentoEstrategico obj where "
                + " obj.planejamentoEstrategico = :p and ";
        for (String atributo : atributos) {
            hql += "lower(obj." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("p", p);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<ItemPlanejamentoEstrategico> listaFiltrandoFilho(String parte, PlanejamentoEstrategico p) {
        String hql = "from ItemPlanejamentoEstrategico obj "
                + " where obj.planejamentoEstrategico = :p and "
                + " lower(obj.nome) like :filtro";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("p", p);
        return q.getResultList();
    }

    public List<ItemPlanejamentoEstrategico> buscarObjetivosPorDescricao(PlanejamentoEstrategico p, ItemPlanejamentoEstrategico ipe) {
        String sql = "select distinct ip.* from ITENS_PLAN ip inner join ProgramaPPA ppa on ip.id = ppa.ITEMPLANEJAMENTOESTRATEGICO_ID " +
            "where ip.PLANEJAMENTOESTRATEGICO_ID = :p and ip.ID = :ipe order by ip.descricao";
        Query q = em.createNativeQuery(sql, ItemPlanejamentoEstrategico.class);
        q.setParameter("p", p.getId());
        q.setParameter("ipe", ipe.getId());
        return q.getResultList();
    }

    public List<ItemPlanejamentoEstrategico> buscarObjetivos(MacroObjetivoEstrategico moe) {
        String sql = "select ip.* from ITENS_PLAN ip where ip.MACROOBJETIVOESTRATEGICO_ID = :moeId ";
        Query q = em.createNativeQuery(sql, ItemPlanejamentoEstrategico.class);
        q.setParameter("moeId", moe.getId());
        return q.getResultList();
    }
}
