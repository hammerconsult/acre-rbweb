/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author reidocrime
 */
public class AlteracaoORCOrigens {
    private String descricaoLinha;
    private BigDecimal excessoArrecadacao;
    private BigDecimal superavit;
    private BigDecimal anulacao;
    private BigDecimal operacaoCredito;
    private BigDecimal total;
    private BigDecimal diferenca;

    public AlteracaoORCOrigens() {
    }

    public AlteracaoORCOrigens(String descricaoLinha, BigDecimal excessoArrecadacao, BigDecimal superavit, BigDecimal anulacao, BigDecimal operacaoCredito, BigDecimal total, BigDecimal diferenca) {
        this.descricaoLinha = descricaoLinha;
        this.excessoArrecadacao = excessoArrecadacao;
        this.superavit = superavit;
        this.anulacao = anulacao;
        this.operacaoCredito = operacaoCredito;
        this.total = total;
        this.diferenca = diferenca;
    }

    public String getDescricaoLinha() {
        return descricaoLinha;
    }

    public void setDescricaoLinha(String descricaoLinha) {
        this.descricaoLinha = descricaoLinha;
    }


    public BigDecimal getAnulacao() {
        return anulacao;
    }

    public void setAnulacao(BigDecimal anulacao) {
        this.anulacao = anulacao;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public BigDecimal getExcessoArrecadacao() {
        return excessoArrecadacao;
    }

    public void setExcessoArrecadacao(BigDecimal excessoArrecadacao) {
        this.excessoArrecadacao = excessoArrecadacao;
    }

    public BigDecimal getOperacaoCredito() {
        return operacaoCredito;
    }

    public void setOperacaoCredito(BigDecimal operacaoCredito) {
        this.operacaoCredito = operacaoCredito;
    }

    public BigDecimal getSuperavit() {
        return superavit;
    }

    public void setSuperavit(BigDecimal superavit) {
        this.superavit = superavit;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

}
