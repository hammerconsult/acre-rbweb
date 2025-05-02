package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 03/03/2015.
 */
public class AcompanhamentoLiberacaoFinanceira {
    private String unidade;
    private BigDecimal orcamento;
    private BigDecimal liberado_1mes;
    private BigDecimal liberado_2mes;
    private BigDecimal liberado_3mes;
    private BigDecimal liberado_4mes;
    private BigDecimal saldoALiberar;

    public AcompanhamentoLiberacaoFinanceira() {
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(BigDecimal orcamento) {
        this.orcamento = orcamento;
    }

    public BigDecimal getLiberado_1mes() {
        return liberado_1mes;
    }

    public void setLiberado_1mes(BigDecimal liberado_1mes) {
        this.liberado_1mes = liberado_1mes;
    }

    public BigDecimal getLiberado_2mes() {
        return liberado_2mes;
    }

    public void setLiberado_2mes(BigDecimal liberado_2mes) {
        this.liberado_2mes = liberado_2mes;
    }

    public BigDecimal getLiberado_3mes() {
        return liberado_3mes;
    }

    public void setLiberado_3mes(BigDecimal liberado_3mes) {
        this.liberado_3mes = liberado_3mes;
    }

    public BigDecimal getLiberado_4mes() {
        return liberado_4mes;
    }

    public void setLiberado_4mes(BigDecimal liberado_4mes) {
        this.liberado_4mes = liberado_4mes;
    }

    public BigDecimal getSaldoALiberar() {
        return saldoALiberar;
    }

    public void setSaldoALiberar(BigDecimal saldoALiberar) {
        this.saldoALiberar = saldoALiberar;
    }
}
