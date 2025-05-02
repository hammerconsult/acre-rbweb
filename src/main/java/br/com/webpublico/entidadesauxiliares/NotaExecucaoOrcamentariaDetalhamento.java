package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class NotaExecucaoOrcamentariaDetalhamento {
    private String conta;
    private String descricao;
    private String evento;
    private BigDecimal valor;

    public NotaExecucaoOrcamentariaDetalhamento() {
        valor = BigDecimal.ZERO;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
