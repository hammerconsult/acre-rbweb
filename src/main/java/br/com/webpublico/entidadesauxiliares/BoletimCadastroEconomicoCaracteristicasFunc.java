package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class BoletimCadastroEconomicoCaracteristicasFunc implements Serializable {
    private String codigoDescricao;
    private String tipo;
    private Long quantidade;
    private String tipoPagamento;

    public BoletimCadastroEconomicoCaracteristicasFunc() {
    }

    public String getCodigoDescricao() {
        return codigoDescricao;
    }

    public void setCodigoDescricao(String codigoDescricao) {
        this.codigoDescricao = codigoDescricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(String tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }
}
