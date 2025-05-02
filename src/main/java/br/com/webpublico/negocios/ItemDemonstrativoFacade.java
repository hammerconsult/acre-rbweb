/*
 * Codigo gerado automaticamente em Wed Apr 20 11:18:16 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ItemDemonstrativoFacade extends AbstractFacade<ItemDemonstrativo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private RelatoriosItemDemonstFacade relatoriosItemDemonstFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemDemonstrativoFacade() {
        super(ItemDemonstrativo.class);
    }

    @Override
    public ItemDemonstrativo salvarRetornando(ItemDemonstrativo itemDemonstrativo) {
        return em.merge(itemDemonstrativo);
    }

    @Override
    public ItemDemonstrativo recuperar(Object id) {
        ItemDemonstrativo itemDemonstrativo = em.find(ItemDemonstrativo.class, id);
        Hibernate.initialize(itemDemonstrativo.getFormulas());
        if (itemDemonstrativo.getFormulas() != null && !itemDemonstrativo.getFormulas().isEmpty()) {
            itemDemonstrativo.getFormulas().forEach(formula -> Hibernate.initialize(formula.getComponenteFormula()));
        }
        return itemDemonstrativo;
    }

    public ItemDemonstrativo recuperaPorGrupo(ItemDemonstrativo itemDemonstrativo, Integer grupo) {
        String hql = "select it from ItemDemonstrativo it " +
            " where it.id = :item and it.grupo = :grupo ";
        Query q = em.createQuery(hql);
        q.setParameter("item", itemDemonstrativo.getId());
        q.setParameter("grupo", grupo);
        if (q.getResultList().isEmpty()) {
            return new ItemDemonstrativo();
        } else {
            return (ItemDemonstrativo) q.getSingleResult();
        }
    }

    public List<ItemDemonstrativo> buscarItensPorExercicioAndRelatorio(Exercicio exercicioCorrente, String filtro, String relatorio, TipoRelatorioItemDemonstrativo tipo) {
        String hql = " SELECT it.* FROM ITEMDEMONSTRATIVO IT "
            + " INNER JOIN RELATORIOSITEMDEMONST rel ON it.RELATORIOSITEMDEMONST_ID = rel.id "
            + " WHERE rel.exercicio_ID = :exerc ";
        if (filtro != null) {
            if (!filtro.equals("")) {
                hql += " and lower(IT.descricao) like \'%" + filtro.toLowerCase() + "%\'";
            }
        }
        if (!relatorio.equals("")) {
            hql += " and lower(rel.descricao) like \'" + relatorio.toLowerCase() + "\' and rel.TIPORELATORIOITEMDEMONSTRATIVO = \'" + tipo.name() + "\'";
        }
        hql += " ORDER BY it.GRUPO, it.ORDEM";
        Query q = getEntityManager().createNativeQuery(hql, ItemDemonstrativo.class);
        q.setParameter("exerc", exercicioCorrente.getId());
        return (List<ItemDemonstrativo>) q.getResultList();
    }

    public List<ItemDemonstrativo> buscarItensPorRelatorio(String filtro, RelatoriosItemDemonst relatorio) {
        String hql = " SELECT IT.* FROM ITEMDEMONSTRATIVO IT "
            + " INNER JOIN RELATORIOSITEMDEMONST rel ON it.RELATORIOSITEMDEMONST_ID = rel.id "
            + " WHERE rel.ID = :relId "
            + " and lower(IT.descricao) like :filtro "
            + " ORDER BY IT.id";
        Query q = getEntityManager().createNativeQuery(hql, ItemDemonstrativo.class);
        q.setParameter("relId", relatorio.getId());
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return (List<ItemDemonstrativo>) q.getResultList();
    }

    public List<ItemDemonstrativo> buscarItensPorRelatorioOrdenadosPorGrupoEOrdem(String filtro, RelatoriosItemDemonst relatorio) {
        if (filtro == null || relatorio == null) return Lists.newArrayList();
        String sql = " select it.* from itemdemonstrativo it "
            + " inner join relatoriositemdemonst rel on it.relatoriositemdemonst_id = rel.id "
            + " where rel.id = :relId "
            + " and (lower(it.nome) like :filtro or lower(it.descricao) like :filtro) "
            + " order by it.grupo, it.ordem ";
        Query q = getEntityManager().createNativeQuery(sql, ItemDemonstrativo.class);
        q.setParameter("relId", relatorio.getId());
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<ItemDemonstrativo> buscarItensDemonstrativosPorDescricaoOrNome(RelatoriosItemDemonst relatorio, String descricao) {
        String sql = " SELECT it.* " +
            " FROM ItemDemonstrativo it " +
            " inner join relatoriositemdemonst rel on it.RELATORIOSITEMDEMONST_ID = rel.id " +
            " inner join exercicio ex on rel.exercicio_id = ex.id " +
            " WHERE  (lower(IT.descricao) LIKE :descricao or lower(it.nome) like :descricao)" +
            " and rel.descricao like :descRelatorio " +
            " and rel.tipoRelatorioItemDemonstrativo = :tipoRelatorio " +
            " and ex.ano = :exercicio ";
        Query q = em.createNativeQuery(sql, ItemDemonstrativo.class);
        q.setParameter("descricao", "%" + descricao.trim().toLowerCase() + "%");
        q.setParameter("descRelatorio", relatorio.getDescricao());
        q.setParameter("tipoRelatorio", relatorio.getTipoRelatorioItemDemonstrativo().name());
        q.setParameter("exercicio", relatorio.getExercicio().getAno() - 1);
        return q.getResultList();
    }

    public boolean hasItemDemonstrativoPorRelatorioOrdemGrupo(RelatoriosItemDemonst rid, Integer ordem, Integer grupo, ItemDemonstrativo it) {
        String sql = " SELECT it.* FROM ItemDemonstrativo it " +
            " WHERE it.RELATORIOSITEMDEMONST_ID = :RELATORIO " +
            " AND it.ORDEM = :ORDEM AND it.GRUPO = :GRUPO ";
        if (it.getId() != null) {
            sql += " AND it.ID <> " + it.getId();
        }
        Query q = em.createNativeQuery(sql, ItemDemonstrativo.class);
        q.setParameter("RELATORIO", rid.getId());
        q.setParameter("ORDEM", ordem);
        q.setParameter("GRUPO", grupo);
        return !q.getResultList().isEmpty();
    }

    public boolean hasItemPorRelatorioAndDescricao(RelatoriosItemDemonst rid, ItemDemonstrativo itemDemonstrativo) {
        String sql = " SELECT it.* FROM ItemDemonstrativo it " +
            " WHERE it.RELATORIOSITEMDEMONST_ID = :RELATORIO " +
            " AND IT.DESCRICAO LIKE :DESCRICAO ";
        if (itemDemonstrativo.getId() != null) {
            sql += " AND it.ID <> " + itemDemonstrativo.getId();
        }
        Query q = em.createNativeQuery(sql, ItemDemonstrativo.class);
        q.setParameter("RELATORIO", rid.getId());
        q.setParameter("DESCRICAO", itemDemonstrativo.getDescricao());
        return !q.getResultList().isEmpty();
    }

    public List<ItemDemonstrativo> buscarItensDemonstrativosPorRelatorio(RelatoriosItemDemonst relatorio) {
        String sql = " SELECT it.* " +
            " FROM ItemDemonstrativo it " +
            " inner join relatoriositemdemonst rel on it.RELATORIOSITEMDEMONST_ID = rel.id " +
            " WHERE  rel.id = :relId ";
        Query q = em.createNativeQuery(sql, ItemDemonstrativo.class);
        q.setParameter("relId", relatorio.getId());
        return q.getResultList();
    }

    public ItemDemonstrativo buscarItemPorDescricaoERelatorio(String descricaoItem, String descricaoRelatorio, TipoRelatorioItemDemonstrativo tipoRelatorio, Integer anoExercicio) {
        if (tipoRelatorio == null) return null;
        String sql = " select it.* " +
            " from itemdemonstrativo it " +
            " inner join relatoriositemdemonst rel on it.relatoriositemdemonst_id = rel.id " +
            " inner join exercicio ex on rel.exercicio_id = ex.id " +
            " where trim(lower(it.descricao)) = :descricaoItem " +
            " and trim(lower(rel.descricao)) = :descricaoRelatorio " +
            " and rel.tiporelatorioitemdemonstrativo = :tipoRelatorio " +
            " and ex.ano = :anoExercicio ";
        Query q = em.createNativeQuery(sql, ItemDemonstrativo.class);
        q.setParameter("descricaoItem", descricaoItem.trim().toLowerCase());
        q.setParameter("descricaoRelatorio", descricaoRelatorio.trim().toLowerCase());
        q.setParameter("tipoRelatorio", tipoRelatorio.name());
        q.setParameter("anoExercicio", anoExercicio);
        List<ItemDemonstrativo> resultado = q.getResultList();
        return !resultado.isEmpty() ? resultado.get(0) : null;
    }

    public RelatoriosItemDemonstFacade getRelatoriosItemDemonstFacade() {
        return relatoriosItemDemonstFacade;
    }

    public EntityManager getEm() {
        return em;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
