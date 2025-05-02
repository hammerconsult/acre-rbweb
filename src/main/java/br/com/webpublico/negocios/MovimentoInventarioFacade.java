/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.InventarioEstoque;
import br.com.webpublico.entidades.ItemInventarioEstoque;
import br.com.webpublico.entidades.Material;
import br.com.webpublico.entidadesauxiliares.ItemInventario;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Fernando
 */
@Stateless
public class MovimentoInventarioFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private MaterialFacade materialFacade;

    private ItemInventario itemInventarioComMaterial(List<ItemInventario> lista, Material material) {
        for (ItemInventario ii : lista) {
            if (ii.getCodigo().equals(material.getCodigo())) {
                return ii;
            }
        }
        return null;
    }

    /**
     * Método que verifica se o inventário está de acordo com o esperado
     *
     * @param inventarioEstoque Inventário esperado
     * @param itensConstatados  Itens contados
     * @return true se estiver de acordo ou false caso contrário
     */
    public boolean existeDiferencaEntreAsListas(InventarioEstoque inventarioEstoque, List<ItemInventario> itensConstatados) {
        if (inventarioEstoque.getItensInventarioEstoque().size() != itensConstatados.size()) {
            return true;
        }
        for (ItemInventarioEstoque iie : inventarioEstoque.getItensInventarioEstoque()) {
            ItemInventario ii = itemInventarioComMaterial(itensConstatados, iie.getMaterial());
            if (ii != null) {
                if (!ii.getQtdeConstatada().equals(iie.getQuantidadeEsperada())) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public void registraInventario(InventarioEstoque inventarioEstoque, List<ItemInventario> itensConstatados) {
        inventarioEstoque = em.find(InventarioEstoque.class, inventarioEstoque.getId());
        for (ItemInventarioEstoque iie : inventarioEstoque.getItensInventarioEstoque()) {
            iie = em.find(ItemInventarioEstoque.class, iie.getId());
            ItemInventario ii = itemInventarioComMaterial(itensConstatados, iie.getMaterial());
            if (ii != null) {
                itensConstatados.remove(ii);
                if (ii.getQtdeConstatada().equals(iie.getQuantidadeEsperada())) {
                    iie.setAjustado(false);
                    iie.setDivergente(false);
                    iie.setEncontrado(false);
                    iie.setQuantidadeConstatada(ii.getQtdeConstatada());
                } else {
                    iie.setAjustado(true);
                    iie.setDivergente(true);
                    iie.setEncontrado(false);
                    iie.setQuantidadeConstatada(ii.getQtdeConstatada());
                }
            } else {
                iie.setAjustado(true);
                iie.setDivergente(true);
                iie.setEncontrado(false);
                iie.setQuantidadeConstatada(BigDecimal.ZERO);
            }
        }
        for (ItemInventario itemEncontrado : itensConstatados) {
            ItemInventarioEstoque iieNovo = new ItemInventarioEstoque();
            iieNovo.setAjustado(true);
            iieNovo.setDivergente(true);
            iieNovo.setEncontrado(true);
            iieNovo.setInventarioEstoque(inventarioEstoque);
//            Material m = materialFacade.buscaMaterialPorCodigo(itemEncontrado.getCodigo());
//            iieNovo.setMaterial(m);
            iieNovo.setQuantidadeConstatada(itemEncontrado.getQtdeConstatada());
            iieNovo.setQuantidadeEsperada(BigDecimal.ZERO);
            em.persist(iieNovo);
        }

    }
}
