package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CotaAbastecimento;
import br.com.webpublico.entidades.ItemContrato;
import br.com.webpublico.entidades.ItemCotaAbastecimento;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.administrativo.frotas.TipoCotaAbastecimento;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 30/09/14
 * Time: 08:48
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class CotaAbastecimentoFacade extends AbstractFacade<CotaAbastecimento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private ItemCotaAbastecimentoFacade itemCotaAbastecimentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;


    public CotaAbastecimentoFacade() {
        super(CotaAbastecimento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public ItemCotaAbastecimentoFacade getItemCotaAbastecimentoFacade() {
        return itemCotaAbastecimentoFacade;
    }

    @Override
    public CotaAbastecimento recuperar(Object id) {
        CotaAbastecimento cotaAbastecimento = super.recuperar(id);
        cotaAbastecimento.setContrato(contratoFacade.recuperar(cotaAbastecimento.getContrato().getId()));
        cotaAbastecimento.getItensCotaAbastecimento().size();
        recuperarItensCotaAbastecimento(cotaAbastecimento.getItensCotaAbastecimento());
        return cotaAbastecimento;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return recuperar(id);
    }

    public void recuperarItensCotaAbastecimento(List<ItemCotaAbastecimento> itensCotaAbastecimento) {
        for (ItemCotaAbastecimento itemCotaAbastecimento : itensCotaAbastecimento) {
            itemCotaAbastecimento = itemCotaAbastecimentoFacade.recuperar(itemCotaAbastecimento.getId());
        }
    }

    public BigDecimal getQuantidadeItemContratoJaUtilizada(CotaAbastecimento cotaAbastecimento, ItemContrato itemContrato) {
        if (cotaAbastecimento.isNormal()) {
            return getQuantidadeItemContratoJaUtilizadaCotaNormal(cotaAbastecimento, itemContrato);
        } else if (cotaAbastecimento.isDistribuicao()) {
            return getQuantidadeItemContratoJaUtilizadaCotaDistribuicao(cotaAbastecimento, itemContrato);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getQuantidadeItemContratoCotaAbastecimento(CotaAbastecimento cotaAbastecimento, ItemCotaAbastecimento itemCotaAbastecimento) {
        ItemContrato itemContrato = itemCotaAbastecimento.getItemContrato();
        if (cotaAbastecimento.isNormal()) {
            if (itemContrato.getControleQuantidade()) {
                return itemContrato.getQuantidadeTotalContrato();
            } else {
                if (itemCotaAbastecimento.getValorUnitario() != null) {
                    return itemContrato.getValorTotal().divide(itemCotaAbastecimento.getValorUnitario(), 2, RoundingMode.HALF_EVEN);
                } else {
                    return itemContrato.getValorTotal();
                }
            }
        }
        String sql = " select  sum(itemcota.quantidadecota) quantidade " +
            "  from cotaabastecimento cota " +
            " inner join itemcotaabastecimento itemcota on itemcota.cotaabastecimento_id = cota.id " +
            "where cota.id = :cota_id " +
            "  and itemcota.itemcontrato_id = :itemcontrato_id ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cota_id", cotaAbastecimento.getCotaAbastecimento().getId());
        q.setParameter("itemcontrato_id", itemContrato.getId());
        return q.getResultList().get(0) != null ? (BigDecimal) q.getResultList().get(0) : BigDecimal.ZERO;
    }

    private BigDecimal getQuantidadeItemContratoJaUtilizadaCotaNormal(CotaAbastecimento cotaAbastecimento, ItemContrato itemContrato) {
        String sql = "select  sum(item_cota.quantidadecota) quantidade " +
            "  from cotaabastecimento cota " +
            " inner join itemcotaabastecimento item_cota on item_cota.cotaabastecimento_id = cota.id " +
            "where cota.tipocotaabastecimento = :tipo_cota " +
            "  and item_cota.itemcontrato_id = :id_itemcontrato ";
        if (cotaAbastecimento.getId() != null) {
            sql += " and item_cota.cotaabastecimento_id <> :id ";
        }

        Query q = em.createNativeQuery(sql);
        if (cotaAbastecimento.getId() != null) {
            q.setParameter("id", cotaAbastecimento.getId());
        }
        q.setParameter("tipo_cota", TipoCotaAbastecimento.NORMAL.name());
        q.setParameter("id_itemcontrato", itemContrato.getId());
        return q.getResultList().get(0) != null ? (BigDecimal) q.getResultList().get(0) : BigDecimal.ZERO;
    }

    private BigDecimal getQuantidadeItemContratoJaUtilizadaCotaDistribuicao(CotaAbastecimento cotaAbastecimento, ItemContrato itemContrato) {
        String sql = " select  sum(item_cota.quantidadecota) quantidade " +
            "  from cotaabastecimento cota " +
            " inner join itemcotaabastecimento item_cota on item_cota.cotaabastecimento_id = cota.id " +
            "where cota.cotaabastecimento_id = :id_cota " +
            "  and item_cota.itemcontrato_id = :id_itemcontrato " +
            " union all " +
            "select sum(item.quantidade) quantidade " +
            "   from abastecimentoobjetofrota abastecimento " +
            "  inner join itemabastecobjetofrota item on item.abastecimentoobjetofrota_id = abastecimento.id " +
            "  inner join itemcotaabastecimento item_cota on item_cota.id = item.itemcotaabastecimento_id " +
            "where abastecimento.cotaabastecimento_id = :id_cota " +
            "  and item_cota.itemcontrato_id = :id_itemcontrato ";
        sql = "select sum(dados.quantidade) from (" + sql + ") dados ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id_cota", cotaAbastecimento.getCotaAbastecimento().getId());
        q.setParameter("id_itemcontrato", itemContrato.getId());
        return q.getResultList().get(0) != null ? (BigDecimal) q.getResultList().get(0) : BigDecimal.ZERO;
    }

    public Integer recuperarProximoNumeroCota(Integer anoCota) {
        String sql = " select max(numeroCota) + 1 from cotaAbastecimento where anoCota = :anoCota ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("anoCota", anoCota);
        return q.getResultList() != null && q.getResultList().get(0) != null ? ((BigDecimal) q.getResultList().get(0)).intValue() : 1;
    }

    public List<CotaAbastecimento> buscarCotasParaAbastecimento(String parte) {
        String hql = " select cota from CotaAbastecimento cota " +
            " where case when cota.tipoCotaAbastecimento = :tipoCotaNormal then cota.unidadeCotista.id " +
            "       else cota.unidadeFilha.id end = :unidade " +
            " and (to_char(anoCota) like :parte " +
            "      or to_char(numeroCota) like :parte" +
            "      or lower(cota.descricao) like :parte) " +
            " order by cota.anoCota, cota.numeroCota desc ";
        Query q = em.createQuery(hql);
        q.setParameter("tipoCotaNormal", TipoCotaAbastecimento.NORMAL);
        q.setParameter("unidade", sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente().getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<CotaAbastecimento> buscarCotaAbastecimentoPorUnidadeCotista(TipoCotaAbastecimento tipoCotaAbastecimento,
                                                                            UnidadeOrganizacional unidadeCotista, String parte) {
        String hql = " select cota from CotaAbastecimento cota " +
            " where cota.tipoCotaAbastecimento =:tipo_cota" +
            " and cota.unidadeCotista.id = :unidade " +
            " and (to_char(anoCota) like :parte " +
            "      or to_char(numeroCota) like :parte " +
            "      or lower(cota.descricao) like :parte) ";
        Query q = em.createQuery(hql);
        q.setParameter("tipo_cota", tipoCotaAbastecimento);
        q.setParameter("unidade", unidadeCotista.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    @Override
    public void salvarNovo(CotaAbastecimento cotaAbastecimento) {
        definirNumeroCotaAbastecimento(cotaAbastecimento);
        super.salvarNovo(cotaAbastecimento);
    }

    private void definirNumeroCotaAbastecimento(CotaAbastecimento cotaAbastecimento) {
        Calendar dataCadastro = Calendar.getInstance();
        dataCadastro.setTime(sistemaFacade.getDataOperacao());
        Integer anoCota = dataCadastro.get(Calendar.YEAR);
        Integer numeroCota = recuperarProximoNumeroCota(anoCota);
        cotaAbastecimento.setAnoCota(anoCota);
        cotaAbastecimento.setNumeroCota(numeroCota);
    }

    public BigDecimal buscarQuantidadeItemCota(ItemCotaAbastecimento itemCotaAbastecimento) {
        if (itemCotaAbastecimento.getCotaAbastecimento().isDistribuicao() ||
            itemCotaAbastecimento.getId() == null) {
            return itemCotaAbastecimento.getQuantidadeCota();
        }
        return itemCotaAbastecimento.getQuantidadeCota().subtract(getQuantidadeUtilizadaCotaDistribuicao(itemCotaAbastecimento));
    }

    public BigDecimal getQuantidadeUtilizadaCotaDistribuicao(ItemCotaAbastecimento itemCotaAbastecimento) {
        String sql = "select sum(itemcota.quantidadecota) " +
            "   from cotaabastecimento cota " +
            "  inner join itemcotaabastecimento itemcota on itemcota.cotaabastecimento_id = cota.id " +
            " where cota.cotaabastecimento_id = :id_cota " +
            "  and itemcota.itemcontrato_id = :id_itemcontrato ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id_cota", itemCotaAbastecimento.getCotaAbastecimento().getId());
        q.setParameter("id_itemcontrato", itemCotaAbastecimento.getItemContrato().getId());
        if (!q.getResultList().isEmpty() && q.getResultList().get(0) != null) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return BigDecimal.ZERO;
    }
}
