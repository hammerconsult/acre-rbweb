/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author juggernaut
 */
public class RelatorioItemOrcamentoFiscal {

    private String descricao;
    private BigDecimal tesouro;
    private BigDecimal outrasFontes;
    private BigDecimal total;

    public RelatorioItemOrcamentoFiscal() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getTesouro() {
        return tesouro;
    }

    public void setTesouro(BigDecimal tesouro) {
        this.tesouro = tesouro;
    }

    public BigDecimal getOutrasFontes() {
        return outrasFontes;
    }

    public void setOutrasFontes(BigDecimal outrasFontes) {
        this.outrasFontes = outrasFontes;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
