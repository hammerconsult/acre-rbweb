package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 09/12/13
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
public class LRFAnexo14DespesaItemRelatorio {

    private String descricaoDespesa;
    private BigDecimal valorDespesa;
    private Integer nivel;

    public LRFAnexo14DespesaItemRelatorio() {
    }

    public String getDescricaoDespesa() {
        return descricaoDespesa;
    }

    public void setDescricaoDespesa(String descricaoDespesa) {
        this.descricaoDespesa = descricaoDespesa;
    }

    public BigDecimal getValorDespesa() {
        return valorDespesa;
    }

    public void setValorDespesa(BigDecimal valorDespesa) {
        this.valorDespesa = valorDespesa;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
