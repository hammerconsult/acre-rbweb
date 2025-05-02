/*
 * Codigo gerado automaticamente em Tue May 24 14:14:23 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class ItemInventarioEstoqueFacade extends AbstractFacade<ItemInventarioEstoque> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private InventarioEstoqueFacade inventarioEstoqueFacade;
    @EJB
    private SaidaMaterialFacade saidaMaterialFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemInventarioEstoqueFacade() {
        super(ItemInventarioEstoque.class);
    }

    @Override
    public ItemInventarioEstoque recuperar(Object id) {
        ItemInventarioEstoque item = em.find(ItemInventarioEstoque.class, id);
        item.getItensInventarioItemEntrada().size();
        item.getItensInventarioItemSaida().size();
        item.getLotesItemInventario().size();
        return item;
    }

    public List<ItemInventarioEstoque> materiaisEstoque(Long id, InventarioEstoque iv) {
        iv.setIniciadoEm(new Date());
        String hql = "SELECT ID FROM MATERIAL m INNER JOIN "
                + "(SELECT MATERIAL_ID, max(DATAESTOQUE) AS de FROM estoque e GROUP BY MATERIAL_ID) consulta "
                + "ON (m.ID=consulta.MATERIAL_ID) INNER JOIN estoque e ON e.MATERIAL_ID = consulta.MATERIAL_ID AND e.dataestoque = consulta.de WHERE e.LOCALESTOQUE_ID = ? AND e.bloqueado = 0";
        Query q = em.createNativeQuery(hql);
        q.setParameter(1, id.toString());
        List<BigDecimal> lista = q.getResultList();
        if (!lista.isEmpty()) {
            for (BigDecimal idEstoque : lista) {
                Estoque e = em.find(Estoque.class, idEstoque.longValue());
//                e.setLiberadoEm(new Date());
                e.setBloqueado(true);
                ItemInventarioEstoque itemInventarioEstoque = new ItemInventarioEstoque();
                itemInventarioEstoque.setMaterial(e.getMaterial());
                itemInventarioEstoque.setQuantidadeEsperada(e.getFisico());
                itemInventarioEstoque.setInventarioEstoque(iv);
                iv.getItensInventarioEstoque().add(itemInventarioEstoque);
            }
            inventarioEstoqueFacade.salvar(iv);
        }
        return iv.getItensInventarioEstoque();
    }

    public List<LoteMaterial> pegaLoteCount(Material m, LocalEstoque l) {
        //String hql = "select lm from LoteMaterial lm, Estoque e, EstoqueLoteMaterial el inner join el.estoque where e.localEstoque = :local and e.material = :mat and lm.material = e.material "; //
        String hql = "select lm from LoteMaterial lm where lm.material = :mat"; //
        Query q = em.createQuery(hql);
        q.setParameter("mat", m);
        //q.setParameter("local", l);
        try {
            return q.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public BigDecimal calcularValorFinanceiroEsperado(ItemInventarioEstoque itemInventarioEstoque) {
        try {
            if (itemInventarioEstoque.getEncontrado()) {
                return itemInventarioEstoque.getFinanceiro();
            } else {
                BigDecimal custoMedio = saidaMaterialFacade.obterCustoMedioConsolidado(itemInventarioEstoque.getMaterial(), itemInventarioEstoque.getInventarioEstoque().getLocalEstoque(), itemInventarioEstoque.getInventarioEstoque().getIniciadoEm());
                return custoMedio.multiply(itemInventarioEstoque.getQuantidadeEsperada());
            }
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal calcularValorFinanceiroConstatado(ItemInventarioEstoque itemInventarioEstoque) {
        try {
            if (itemInventarioEstoque.getEncontrado()) {
                return itemInventarioEstoque.getFinanceiro();
            } else {
                BigDecimal custoMedio = saidaMaterialFacade.obterCustoMedioConsolidado(itemInventarioEstoque.getMaterial(), itemInventarioEstoque.getInventarioEstoque().getLocalEstoque(), itemInventarioEstoque.getInventarioEstoque().getIniciadoEm());
                if (itemInventarioEstoque.getQuantidadeConstatada() != null) {
                    BigDecimal vlFinanceiro = custoMedio.multiply(itemInventarioEstoque.getQuantidadeConstatada());
                    itemInventarioEstoque.setFinanceiro(vlFinanceiro);
                    return itemInventarioEstoque.getFinanceiro();
                }
                return BigDecimal.ZERO;
            }
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
