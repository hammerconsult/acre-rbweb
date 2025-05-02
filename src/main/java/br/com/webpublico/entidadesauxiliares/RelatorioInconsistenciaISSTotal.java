package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Fabio on 03/09/2018.
 */
public class RelatorioInconsistenciaISSTotal {
    private String descricao;
    private int quantidade;
    private BigDecimal valor;

    public RelatorioInconsistenciaISSTotal() {
        this.quantidade = 0;
        this.valor = BigDecimal.ZERO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void addValor(BigDecimal valor) {
        this.valor = this.valor.add(valor);
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void addQuantidade(int quantidade) {
        this.quantidade += quantidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelatorioInconsistenciaISSTotal total = (RelatorioInconsistenciaISSTotal) o;

        return !(descricao != null ? !descricao.equals(total.descricao) : total.descricao != null);
    }

    @Override
    public int hashCode() {
        return descricao != null ? descricao.hashCode() : 0;
    }
}
