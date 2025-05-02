package br.com.webpublico.entidadesauxiliares.rh.relatorio;

import java.io.Serializable;
import java.math.BigDecimal;

public class RelatorioAcompanhamentoAtualizacaoCadastralEstatistica implements Serializable {
    private String mes;
    private Integer quantidadeConvocados;
    private BigDecimal percentualAtualizacao;
    private Integer totalAtualizados;
    private Integer totalNaoAtualizados;

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Integer getQuantidadeConvocados() {
        return quantidadeConvocados;
    }

    public void setQuantidadeConvocados(Integer quantidadeConvocados) {
        this.quantidadeConvocados = quantidadeConvocados;
    }


    public BigDecimal getPercentualAtualizacao() {
        return percentualAtualizacao;
    }

    public void setPercentualAtualizacao(BigDecimal percentualAtualizacao) {
        this.percentualAtualizacao = percentualAtualizacao;
    }

    public Integer getTotalAtualizados() {
        return totalAtualizados;
    }

    public void setTotalAtualizados(Integer totalAtualizados) {
        this.totalAtualizados = totalAtualizados;
    }

    public Integer getTotalNaoAtualizados() {
        return totalNaoAtualizados;
    }

    public void setTotalNaoAtualizados(Integer totalNaoAtualizados) {
        this.totalNaoAtualizados = totalNaoAtualizados;
    }
}
