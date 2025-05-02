package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemDoctoItemEntrada;

import java.math.BigDecimal;

public class ItemDoctoItemEntradaVO {

    private ItemDoctoItemEntrada itemDoctoItemEntrada;
    private BigDecimal valorLiquidadoDocumento;

    public ItemDoctoItemEntrada getItemDoctoItemEntrada() {
        return itemDoctoItemEntrada;
    }

    public void setItemDoctoItemEntrada(ItemDoctoItemEntrada itemDoctoItemEntrada) {
        this.itemDoctoItemEntrada = itemDoctoItemEntrada;
    }

    public BigDecimal getValorLiquidadoDocumento() {
        return valorLiquidadoDocumento;
    }

    public void setValorLiquidadoDocumento(BigDecimal valorLiquidadoDocumento) {
        this.valorLiquidadoDocumento = valorLiquidadoDocumento;
    }
}
