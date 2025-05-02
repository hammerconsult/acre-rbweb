/*
 * Codigo gerado automaticamente em Mon Apr 09 21:04:03 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.entidades.TributoTaxaDividasDiversas;
import br.com.webpublico.enums.SituacaoTributoTaxaDividasDiversas;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TributoTaxasDividasDiversasFacade extends AbstractFacade<TributoTaxaDividasDiversas> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TributoTaxasDividasDiversasFacade() {
        super(TributoTaxaDividasDiversas.class);
    }

    public boolean existeTributoParaTaxa(TributoTaxaDividasDiversas tributoTaxaDividasDiversas) {
        String sql = "select tribdiv.* from tributotaxadividasdiversas tribdiv " +
            " where tribdiv.tributo_id = :idTributo " +
            " and tribdiv.situacaotributo = :situacao ";
        if (tributoTaxaDividasDiversas.getId() != null) {
            sql += " and tribdiv.id != :idTributoDividaDiversa";
        }
        Query q = em.createNativeQuery(sql, TributoTaxaDividasDiversas.class);
        q.setParameter("idTributo", tributoTaxaDividasDiversas.getTributo().getId());
        q.setParameter("situacao", SituacaoTributoTaxaDividasDiversas.ATIVO.name());
        if (tributoTaxaDividasDiversas.getId() != null) {
            q.setParameter("idTributoDividaDiversa", tributoTaxaDividasDiversas.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public List<TributoTaxaDividasDiversas> listaTributosAtivos(String s) {
        String hql = "from TributoTaxaDividasDiversas obj where "
            + " lower(obj.tributo.descricao) like :filtro "
            + " and obj.situacaoTributo = :situacao";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("situacao", SituacaoTributoTaxaDividasDiversas.ATIVO);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<TributoTaxaDividasDiversas> listaOrdenada() {
        String hql = "from TributoTaxaDividasDiversas obj where " +
            " obj.situacaoTributo = :situacao order by obj.tributo.descricao";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("situacao", SituacaoTributoTaxaDividasDiversas.ATIVO);
        return q.getResultList();
    }

    public boolean parcelaComTaxaBloqueada(Long idParcela) {
        String sql = "select  pvd.id from parcelavalordivida pvd \n" +
            "inner join itemparcelavalordivida ipvd on ipvd.parcelavalordivida_id = pvd.id\n" +
            "inner join itemvalordivida ivd on ivd.id = ipvd.itemvalordivida_id\n" +
            "inner join tributo tr on tr.id = ivd.tributo_id\n" +
            "inner join tributotaxadividasdiversas trtx on trtx.tributo_id = tr.id\n" +
            "where pvd.id = :parcela and coalesce(trtx.bloqueiacertidao,0) = :bloqueio";
        Query q = em.createNativeQuery(sql);
        q.setParameter("bloqueio", true);
        q.setParameter("parcela", idParcela);
        return !q.getResultList().isEmpty();
    }

    public boolean parcelaComAlvaraBloqueada(Long idParcela) {
        String sql = "select  pvd.id from parcelavalordivida pvd \n" +
            "inner join itemparcelavalordivida ipvd on ipvd.parcelavalordivida_id = pvd.id\n" +
            "inner join itemvalordivida ivd on ivd.id = ipvd.itemvalordivida_id\n" +
            "inner join tributo tr on tr.id = ivd.tributo_id\n" +
            "inner join tributotaxadividasdiversas trtx on trtx.tributo_id = tr.id\n" +
            "where pvd.id = :parcela and coalesce(trtx.bloqueiaalvara,0) = :bloqueio";
        Query q = em.createNativeQuery(sql);
        q.setParameter("bloqueio", true);
        q.setParameter("parcela", idParcela);
        return !q.getResultList().isEmpty();
    }

    public TributoTaxaDividasDiversas buscarTributoDividaDiversaPorTributo(Tributo tributo) {
        String sql = "select tt.* from TributoTaxaDividasDiversas tt " +
            " where tt.tributo_id = :idTributo and tt.situacaotributo = :situacao";
        Query q = em.createNativeQuery(sql, TributoTaxaDividasDiversas.class);
        q.setParameter("idTributo", tributo.getId());
        q.setParameter("situacao", SituacaoTributoTaxaDividasDiversas.ATIVO.name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (TributoTaxaDividasDiversas) resultList.get(0);
        }
        return null;
    }
}
