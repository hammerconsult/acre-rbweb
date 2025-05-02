package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by HardRock on 26/05/2017.
 */
public class SaidaMaterialNaoUtilizado {

    private String unidadeAdministrativa;
    private String localEstoque;
    private String material;
    private String tipoBaixa;
    private BigDecimal valor;

    public SaidaMaterialNaoUtilizado() {
        valor = BigDecimal.ZERO;
    }

    public String getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(String unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
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

    public String getTipoBaixa() {
        return tipoBaixa;
    }

    public void setTipoBaixa(String tipoBaixa) {
        this.tipoBaixa = tipoBaixa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
