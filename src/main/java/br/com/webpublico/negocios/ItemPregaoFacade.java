/*
 * Codigo gerado automaticamente em Thu Dec 15 11:56:46 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.StatusLancePregao;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ItemPregaoFacade extends AbstractFacade<ItemPregao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemPregaoFacade() {
        super(ItemPregao.class);
    }

    @Override
    public ItemPregao recuperar(Object id) {
        ItemPregao itemPregao = em.find(ItemPregao.class, id);
        Hibernate.initialize(itemPregao.getListaDeRodadaPregao());
        for (RodadaPregao rodadaPregao : itemPregao.getListaDeRodadaPregao()) {
            Hibernate.initialize(rodadaPregao.getListaDeLancePregao());
        }
        return itemPregao;
    }

    public BigDecimal getPercentualDescontoLanceVencedor(ItemProcessoDeCompra item) {
        String sql = " " +
            " select coalesce(iplv.percentualdesconto, 0) " +
            "  from itempregao ip " +
            "   left join itpreitpro itip on itip.itempregao_id = ip.id " +
            "   left join itprelotpro iplp on iplp.itempregao_id = ip.id " +
            "   left join ItemPregaoLoteItemProcesso itemlote on itemlote.itempregaoloteprocesso_id = iplp.id " +
            "   left join itemprocessodecompra ipc on coalesce(itip.itemprocessodecompra_id, itemlote.itemprocessodecompra_id) = ipc.id " +
            "   inner join itempregaolancevencedor iplv on iplv.id = ip.itempregaolancevencedor_id " +
            " where ipc.id = :idIpc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idIpc", item.getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public List<ItemPregao> buscarItensVencidosPorFornecedor(Licitacao licitacao, PropostaFornecedor proposta) {
        String sql = " select item.* from pregao pre " +
            "  inner join itempregao item on item.pregao_id = pre.id " +
            "  inner join rodadapregao rodada on rodada.itempregao_id = item.id " +
            "  inner join lancepregao lance on lance.rodadapregao_id = rodada.id " +
            "  inner join ITEMPREGAOLANCEVENCEDOR iplv on iplv.LANCEPREGAO_ID = lance.ID " +
            " where iplv.STATUS = :vencedor " +
            " and pre.licitacao_id = :idLicitacao ";
        sql += proposta != null ? " and lance.propostafornecedor_id = :idProposta " : "";
        Query q = em.createNativeQuery(sql, ItemPregao.class);
        q.setParameter("idLicitacao", licitacao.getId());
        if (proposta != null) {
            q.setParameter("idProposta", proposta.getId());
        }
        q.setParameter("vencedor", StatusLancePregao.VENCEDOR.name());
        return q.getResultList();
    }

    public List<ItemPregao> buscarItemPregaoPorItemProcessoCompra(Licitacao licitacao, List<Long> idsItemProcesso, PropostaFornecedor proposta) {
        String sql = " select ip.* from itempregao ip " +
            "           inner join pregao p on p.id = ip.pregao_id " +
            "           inner join itpreitpro ipp on ipp.itempregao_id = ip.id " +
            "           inner join itempregaolancevencedor iplv on iplv.itempregao_id = ip.id " +
            "           inner join lancepregao lp on lp.id = iplv.lancepregao_id " +
            "         where p.licitacao_id = :idLicitacao " +
            "           and ipp.itemprocessodecompra_id in (:idsItem) ";
        sql += proposta != null ? " and lp.propostafornecedor_id = :idProposta " : "";
        Query q = em.createNativeQuery(sql, ItemPregao.class);
        q.setParameter("idLicitacao", licitacao.getId());
        if (proposta != null) {
            q.setParameter("idProposta", proposta.getId());
        }
        q.setParameter("idsItem", idsItemProcesso);
        return q.getResultList();
    }
}
