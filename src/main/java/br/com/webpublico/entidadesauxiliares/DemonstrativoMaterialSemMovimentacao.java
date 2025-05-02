package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by HardRock on 25/05/2017.
 */
public class DemonstrativoMaterialSemMovimentacao {

    private String unidade;
    private String localEstoque;
    private String material;
    private Integer item;
    private BigDecimal valor;

    public DemonstrativoMaterialSemMovimentacao() {
        valor = BigDecimal.ZERO;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(String localEstoque) {
        this.localEstoque = localEstoque;
    }


    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
