package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemFichaFinanceiraFP;
import br.com.webpublico.entidades.LancamentoFP;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 10/10/14
 * Time: 14:57
 */
public class ItemConsignacao implements Serializable{

    private ItemFichaFinanceiraFP itemFichaFinanceiraFP;
    private LancamentoFP lancamentoFP;
    private boolean naoPago;

    public ItemConsignacao() {
        naoPago = false;
    }

    public ItemConsignacao(ItemFichaFinanceiraFP itemFichaFinanceiraFP, LancamentoFP lancamentoFP) {
        this.itemFichaFinanceiraFP = itemFichaFinanceiraFP;
        this.lancamentoFP = lancamentoFP;
    }

    public ItemFichaFinanceiraFP getItemFichaFinanceiraFP() {
        return itemFichaFinanceiraFP;
    }

    public void setItemFichaFinanceiraFP(ItemFichaFinanceiraFP itemFichaFinanceiraFP) {
        this.itemFichaFinanceiraFP = itemFichaFinanceiraFP;
    }

    public boolean isNaoPago() {
        return naoPago;
    }

    public void setNaoPago(boolean naoPago) {
        this.naoPago = naoPago;
    }

    public LancamentoFP getLancamentoFP() {
        return lancamentoFP;
    }

    public void setLancamentoFP(LancamentoFP lancamentoFP) {
        this.lancamentoFP = lancamentoFP;
    }
}
