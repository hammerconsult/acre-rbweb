/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.InsumoAplicado;
import br.com.webpublico.entidades.ItemProduzido;

import java.util.List;

/**
 * @author terminal1
 */
public class ControleProducao {

    private ItemProduzido itemProduzido;
    private List<InsumoAplicado> insumosAplicado;

    public List<InsumoAplicado> getInsumosAplicado() {
        return insumosAplicado;
    }

    public void setInsumosAplicado(List<InsumoAplicado> insumosAplicado) {
        this.insumosAplicado = insumosAplicado;
    }


    public ItemProduzido getItemProduzido() {
        return itemProduzido;
    }

    public void setItemProduzido(ItemProduzido itemProduzido) {
        this.itemProduzido = itemProduzido;
    }


}
